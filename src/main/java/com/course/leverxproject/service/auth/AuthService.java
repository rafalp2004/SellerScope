package com.course.leverxproject.service.auth;

import com.course.leverxproject.dto.user.LoginRequestDTO;
import com.course.leverxproject.dto.user.LoginResponseDTO;
import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.entity.User;

public interface AuthService {

    UserResponseDTO createSeller(UserCreateRequestDTO userDTO);

    User createAnonymous();

    void approveSeller(int userId);

    LoginResponseDTO verify(LoginRequestDTO loginDTO);
}
