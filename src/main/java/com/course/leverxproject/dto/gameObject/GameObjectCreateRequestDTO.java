package com.course.leverxproject.dto.gameObject;

import com.course.leverxproject.enums.GameEnum;

public record GameObjectCreateRequestDTO(
        String title,
        String text,
        GameEnum game
) {
}
