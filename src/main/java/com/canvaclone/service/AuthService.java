package com.canvaclone.service;

import com.canvaclone.dto.AuthDto;
import com.canvaclone.entity.User;
import com.canvaclone.exception.BadRequestException;
import com.canvaclone.exception.UnauthorizedException;
import com.canvaclone.repository.UserRepository;
import com.canvaclone.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private final ConcurrentHashMap<String, String> otpCache = new ConcurrentHashMap<>();

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .roles(Set.of("ROLE_USER"))
                .build();

        User savedUser = userRepository.save(user);
        
        // Generate a simple verification token (could be a JWT or random string)
        String verificationToken = jwtUtil.generateToken(savedUser.getEmail());
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(), savedUser.getPassword(), Set.of()));

        return AuthDto.AuthResponse.builder()
                .token(token)
                .user(userService.mapToResponse(savedUser))
                .build();
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            
            if (!user.isEmailVerified()) {
                throw new UnauthorizedException("Please verify your email before logging in");
            }

            String token = jwtUtil.generateToken(userDetails);

            return AuthDto.AuthResponse.builder()
                    .token(token)
                    .user(userService.mapToResponse(user))
                    .build();
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    public AuthDto.ApiResponse verifyEmail(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException("Invalid verification token"));
            
            user.setEmailVerified(true);
            userRepository.save(user);
            
            return AuthDto.ApiResponse.builder()
                    .success(true)
                    .message("Email verified successfully")
                    .build();
        } catch (Exception e) {
            throw new BadRequestException("Invalid or expired verification token");
        }
    }

    public AuthDto.ApiResponse forgotPassword(AuthDto.ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found with this email"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpCache.put(user.getEmail(), otp);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return AuthDto.ApiResponse.builder()
                .success(true)
                .message("OTP sent to your email")
                .build();
    }

    public AuthDto.ApiResponse resetPassword(AuthDto.ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found with this email"));

        String validOtp = otpCache.get(request.getEmail());
        if (validOtp == null || !validOtp.equals(request.getOtp())) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpCache.remove(request.getEmail());

        return AuthDto.ApiResponse.builder()
                .success(true)
                .message("Password reset successfully")
                .build();
    }
}
