package com.course.leverxproject.service.auth;

import com.course.leverxproject.dto.user.*;
import com.course.leverxproject.entity.User;

public interface AuthService {

    UserResponseDTO createSeller(UserCreateRequestDTO userDTO);

    User createAnonymous();

    void approveSeller(int userId);

    LoginResponseDTO login(LoginRequestDTO loginDTO);

    UserResponseDTO verifyAccount(String email, String code);

    void forgotPassword(String email);

    UserResponseDTO resetPassword(ResetPasswordDTO resetPasswordDTO);
}
