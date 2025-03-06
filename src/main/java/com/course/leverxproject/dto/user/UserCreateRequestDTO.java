package com.course.leverxproject.dto.user;

public record UserCreateRequestDTO(
        String firstName,
        String lastName,
        String password,
        String email
) {
}
