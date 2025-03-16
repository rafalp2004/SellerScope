package com.course.leverxproject.dto.user;

import java.time.LocalDateTime;


public record UserResponseDTO(
        int id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt,
        double rating,
        Boolean approved


) {
}
