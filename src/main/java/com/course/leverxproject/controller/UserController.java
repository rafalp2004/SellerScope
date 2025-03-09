package com.course.leverxproject.controller;

import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.enums.GameEnum;
import com.course.leverxproject.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
public class UserController {

    //Creating, updating seller and changing password is in AuthController
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getSellers(
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
                throw new IllegalArgumentException(e);
            }
        }

        List<UserResponseDTO> sellers = userService.findAll(page, size, sortBy, sortDir, gameFilter, minRating, maxRating);
        return new ResponseEntity<>(sellers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getSeller(@PathVariable int id) {
        UserResponseDTO seller = userService.findById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

}
