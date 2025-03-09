package com.course.leverxproject.controller;

import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.service.auth.AuthService;
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

    public AuthController(AuthService authService) {
        this.authService = authService;
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

    //TODO only for admins
    @PostMapping("{userId}/approve")
    public ResponseEntity<Void> approveSeller(@PathVariable int userId) {
        authService.approveSeller(userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    //TODO verifying by email and changing password

}
