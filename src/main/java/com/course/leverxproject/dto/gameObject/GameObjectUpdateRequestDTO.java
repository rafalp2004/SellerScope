package com.course.leverxproject.dto.gameObject;

import com.course.leverxproject.enums.GameEnum;

public record GameObjectUpdateRequestDTO(
        String title,
        String text,
        GameEnum game
) {
}
