package com.course.leverxproject.dto.user;

import java.time.LocalDateTime;


public record UserResponseDTO(
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt

) {
}
