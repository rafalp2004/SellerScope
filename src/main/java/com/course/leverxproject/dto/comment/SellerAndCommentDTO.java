package com.course.leverxproject.dto.comment;

import com.course.leverxproject.dto.user.UserCreateRequestDTO;

public record SellerAndCommentDTO(
        UserCreateRequestDTO sellerDTO,
        CommentCreateRequestDTO commentDTO
) {
}
