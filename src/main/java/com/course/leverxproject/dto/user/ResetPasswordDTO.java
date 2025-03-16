package com.course.leverxproject.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "New password must not be blank")
        String newPassword,

        @NotBlank(message = "Code must not be blank")
        String code
) {
}
