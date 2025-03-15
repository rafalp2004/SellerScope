package com.course.leverxproject.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password must not be blank")
        String password
) {
}
