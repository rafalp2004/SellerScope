package com.course.leverxproject.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        int id,
        String message,
        short rate,
        String author,
        int sellerId,
        String seller,
        LocalDateTime createdAt,
        Boolean approved
) {
}
