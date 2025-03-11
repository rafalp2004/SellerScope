package com.course.leverxproject.dto.comment;

public record CommentResponseWithTokenDTO(
        CommentResponseDTO commentResponseDTO,
        String token
) {
}
