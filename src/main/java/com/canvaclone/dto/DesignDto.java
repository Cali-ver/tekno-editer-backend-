package com.canvaclone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class DesignDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private Integer width;
        private Integer height;
        private String jsonData;
        private String thumbnail;
        private Boolean isPublic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String title;
        private String jsonData;
        private String thumbnail;
        private String accessLevel;
        private Boolean isPublic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DesignResponse {
        private Long id;
        private String title;
        private Integer width;
        private Integer height;
        private String thumbnail;
        private String jsonData;
        private String accessLevel;
        private String ownerEmail;
        private Boolean isPublic;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
