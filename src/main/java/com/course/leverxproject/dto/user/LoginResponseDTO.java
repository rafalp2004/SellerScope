package com.course.leverxproject.dto.user;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        String token,
        int id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
) {
}
