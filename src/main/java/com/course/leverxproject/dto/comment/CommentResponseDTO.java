package com.course.leverxproject.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        String message,
        short rate,
        String author,
        String seller,
        LocalDateTime createdAt,
        Boolean approved
) {
}
