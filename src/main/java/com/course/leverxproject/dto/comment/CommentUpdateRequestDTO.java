package com.course.leverxproject.dto.comment;

public record CommentUpdateRequestDTO(
        String message,
        short rate
) {
}
