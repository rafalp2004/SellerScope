package com.course.leverxproject.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequestDTO(
        @NotNull(message = "Message must not be null")
        String message,

        @Min(value = 0, message = "Rate must be at least 0")
        @Max(value = 10, message = "Rate must be at most 10")
        short rate
) {
}
