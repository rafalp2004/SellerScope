package com.course.leverxproject.dto.gameObject;

import java.time.LocalDateTime;

public record GameObjectResponseDTO(
        int id,
        String title,
        String text,
        int userId,
        String user,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
