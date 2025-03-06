package com.course.leverxproject.service.auth;

import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;

public interface AuthService {

    UserResponseDTO createSeller(UserCreateRequestDTO userDTO);
}
