package com.canvaclone.service;

import com.canvaclone.dto.UserDto;
import com.canvaclone.entity.User;
import com.canvaclone.exception.ResourceNotFoundException;
import com.canvaclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.canvaclone.entity.Provider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Collections;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto.UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    public User findOrCreateOAuthUser(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");
        String picture = oauthUser.getAttribute("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getProvider() == Provider.LOCAL) {
                // Link account
                user.setProvider(Provider.GOOGLE);
                user.setGoogleId(googleId);
                user.setAvatarUrl(picture);
                user.setEmailVerified(true);
            } else {
                // Already GOOGLE provider, just update picture if changed
                user.setAvatarUrl(picture);
                user.setEmailVerified(true);
            }
        } else {
            // New user
            user = User.builder()
                    .email(email)
                    .name(name)
                    .googleId(googleId)
                    .avatarUrl(picture)
                    .provider(Provider.GOOGLE)
                    .emailVerified(true)
                    .roles(Collections.singleton("ROLE_USER"))
                    .build();
        }

        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public UserDto.UserResponse updateProfile(String email, UserDto.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getAvatarUrl() != null)
            user.setAvatarUrl(request.getAvatarUrl());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public UserDto.UserResponse mapToResponse(User user) {
        return UserDto.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getProvider())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
