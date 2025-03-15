package com.course.leverxproject.dto.comment;

public record CommentCreateRequestDTO(
        String message,
        short rate
) {
}
