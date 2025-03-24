package com.course.leverxproject.controller;

import com.course.leverxproject.dto.user.*;
import com.course.leverxproject.exception.user.VerificationException;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.redis.RedisService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService, RedisService redisService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> createSeller(@RequestBody @Valid UserCreateRequestDTO userDTO) {
        UserResponseDTO userResponseDTO = authService.createSeller(userDTO);
        EntityModel<UserResponseDTO> entityModel = EntityModel.of(
                userResponseDTO,
                linkTo(methodOn(UserController.class).getSeller(userResponseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSellers(0, 10, "rating", "dsc", null, 0, 10))
                        .withRel("sellers"));


        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @PostMapping("{userId}/approve")
    public ResponseEntity<Void> approveSeller(@PathVariable int userId) {
        authService.approveSeller(userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
        authService.forgotPassword(forgotPasswordDTO.email());
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/reset")
    public ResponseEntity<EntityModel<UserResponseDTO>> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        UserResponseDTO userResponseDTO = authService.resetPassword(resetPasswordDTO);
        EntityModel<UserResponseDTO> entityModel = EntityModel.of(
                userResponseDTO,
                linkTo(methodOn(UserController.class).getSeller(userResponseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSellers(0, 10, "rating", "dsc", null, 0, 10))
                        .withRel("sellers"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<EntityModel<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO userResponseDTO = authService.login(loginRequestDTO);
        EntityModel<LoginResponseDTO> entityModel = EntityModel.of(
                userResponseDTO,
                linkTo(methodOn(UserController.class).getSeller(userResponseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSellers(0, 10, "rating", "dsc", null, 0, 10))
                        .withRel("sellers"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @GetMapping("/verify")
    ResponseEntity<EntityModel<UserResponseDTO>> verify(@RequestParam String email, @RequestParam String code) {
        UserResponseDTO userResponseDTO = authService.verifyAccount(email, code);
        if (userResponseDTO == null) {
            throw new VerificationException("Invalid or expired verification code.");
        }
        EntityModel<UserResponseDTO> entityModel = EntityModel.of(
                userResponseDTO,
                linkTo(methodOn(UserController.class).getSeller(userResponseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSellers(0, 10, "rating", "dsc", null, 0, 10))
                        .withRel("sellers"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

}
