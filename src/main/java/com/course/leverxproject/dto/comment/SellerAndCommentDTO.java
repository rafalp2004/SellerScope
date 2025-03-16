package com.course.leverxproject.dto.comment;

import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record SellerAndCommentDTO(
        @NotNull(message = "Seller DTO must not be null")
        @Valid
        UserCreateRequestDTO sellerDTO,

        @NotNull(message = "Comment DTO must not be null")
        @Valid
        CommentCreateRequestDTO commentDTO
) {
}