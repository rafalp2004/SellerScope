package com.course.leverxproject.dto.gameObject;

import com.course.leverxproject.enums.GameEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameObjectCreateRequestDTO(
        @NotBlank(message = "Title must not be blank")
        String title,

        @NotBlank(message = "Text must not be blank")
        String text,

        @NotNull(message = "Game must not be null")
        GameEnum game
) {
}
