package com.course.leverxproject.controller;

import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.enums.GameEnum;
import com.course.leverxproject.service.user.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/sellers")
public class UserController {

    //Creating and changing password is in AuthController
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "dsc") String sortDir,
            @RequestParam(required = false) String game,
            @RequestParam(defaultValue = "0") double minRating,
            @RequestParam(defaultValue = "10") double maxRating
    ) {
        //In case when we want to filter sellers by game
        GameEnum gameFilter = null;
        if (game != null && !game.isEmpty()) {
            try {
                gameFilter = GameEnum.valueOf(game);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Wrong type of game", e);
            }
        }

        List<UserResponseDTO> sellers = userService.findAll(page, size, sortBy, sortDir, gameFilter, minRating, maxRating);

        List<EntityModel<UserResponseDTO>> sellerResources = sellers.stream()
                .map(seller -> EntityModel.of(seller,
                        linkTo(methodOn(UserController.class).getSeller(seller.id())).withSelfRel(),
                        linkTo(methodOn(UserController.class)
                                .getSellers(page, size, sortBy, sortDir, game, minRating, maxRating))
                                .withRel("sellers")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserResponseDTO>> collectionModel = CollectionModel.of(sellerResources,
                linkTo(methodOn(UserController.class)
                        .getSellers(page, size, sortBy, sortDir, game, minRating, maxRating))
                        .withSelfRel());

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getSeller(@PathVariable int id) {
        UserResponseDTO seller = userService.findById(id);
        EntityModel<UserResponseDTO> entityModel = EntityModel.of(
                seller,
                linkTo(methodOn(UserController.class).getSeller(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getSellers(0, 10, "rating", "dsc", null, 0, 10))
                        .withRel("sellers"));


        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

}
