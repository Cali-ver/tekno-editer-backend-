package com.canvaclone.service;

import com.canvaclone.dto.UserDto;
import com.canvaclone.entity.User;
import com.canvaclone.exception.ResourceNotFoundException;
import com.canvaclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

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
