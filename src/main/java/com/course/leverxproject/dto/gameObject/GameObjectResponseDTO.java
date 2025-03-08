package com.course.leverxproject.dto.gameObject;

import java.time.LocalDateTime;

public record GameObjectResponseDTO(
        String title,
        String text,
        String user,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
