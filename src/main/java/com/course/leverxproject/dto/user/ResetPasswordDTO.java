package com.course.leverxproject.dto.user;

public record ResetPasswordDTO(
        String email,
        String newPassword,
        String code
) {
}
