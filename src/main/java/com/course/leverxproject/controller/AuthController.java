package com.course.leverxproject.controller;

import com.course.leverxproject.dto.user.LoginRequestDTO;
import com.course.leverxproject.dto.user.LoginResponseDTO;
import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.exception.user.VerificationException;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.redis.RedisService;
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
    private final RedisService redisService;

    public AuthController(AuthService authService, RedisService redisService) {
        this.authService = authService;
        this.redisService = redisService;
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> createSeller(@RequestBody UserCreateRequestDTO userDTO) {
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
    //TODO add verifying by email and changing password

    @PostMapping("/login")
    public ResponseEntity<EntityModel<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO userResponseDTO = authService.verify(loginRequestDTO);
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
        if(userResponseDTO==null){
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
