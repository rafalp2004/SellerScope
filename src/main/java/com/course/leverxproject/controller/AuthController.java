package com.course.leverxproject.controller;

import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createSeller(@RequestBody UserCreateRequestDTO userDTO){
        UserResponseDTO userResponseDTO = authService.createSeller(userDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }
}
