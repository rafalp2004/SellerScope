package com.course.leverxproject.controller;


import com.course.leverxproject.dto.gameObject.GameObjectCreateRequestDTO;
import com.course.leverxproject.dto.gameObject.GameObjectResponseDTO;
import com.course.leverxproject.dto.gameObject.GameObjectUpdateRequestDTO;
import com.course.leverxproject.service.gameObject.GameObjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @PostMapping
    private ResponseEntity<EntityModel<GameObjectResponseDTO>> createObject(@RequestBody @Valid GameObjectCreateRequestDTO gameDTO) {
        GameObjectResponseDTO responseDTO = gameObjectService.create(gameDTO);
        log.info(responseDTO.toString());
        EntityModel<GameObjectResponseDTO> entityModel = EntityModel.of(
                responseDTO,
                linkTo(methodOn(GameObjectController.class).getObject(responseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(responseDTO.userId())).withRel("seller"),
                linkTo(methodOn(CommentController.class).getComments(responseDTO.userId(), 0, 10, "rate", "dsc")).withRel("userComments")
        );
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<GameObjectResponseDTO>> updateObject(@PathVariable int id, @RequestBody @Valid GameObjectUpdateRequestDTO gameDTO) {
        GameObjectResponseDTO responseDTO = gameObjectService.update(id, gameDTO);
        EntityModel<GameObjectResponseDTO> entityModel = EntityModel.of(
                responseDTO,
                linkTo(methodOn(GameObjectController.class).getObject(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(responseDTO.userId())).withRel("user"),
                linkTo(methodOn(CommentController.class)
                        .getComments(responseDTO.userId(), 0, 10, "rate", "dsc"))
                        .withRel("userComments")
        );
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GameObjectResponseDTO>> getObject(@PathVariable int id) {
        GameObjectResponseDTO responseDTO = gameObjectService.getById(id);
        EntityModel<GameObjectResponseDTO> entityModel = EntityModel.of(
                responseDTO,
                linkTo(methodOn(GameObjectController.class).getObject(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(responseDTO.userId())).withRel("user"),
                linkTo(methodOn(CommentController.class)
                        .getComments(responseDTO.userId(), 0, 10, "rate", "dsc"))
                        .withRel("userComments")
        );
        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GameObjectResponseDTO>>> getObjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "dsc") String sortDir
    ) {
        List<GameObjectResponseDTO> responseDTO = gameObjectService.getAll(page, size, sortBy, sortDir);

        List<EntityModel<GameObjectResponseDTO>> entityModelList = responseDTO.stream()
                .map(gameObject -> EntityModel.of(gameObject,
                        linkTo(methodOn(GameObjectController.class).getObject(gameObject.id())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getSeller(gameObject.userId())).withRel("user"),
                        linkTo(methodOn(CommentController.class)
                                .getComments(gameObject.userId(), 0, 10, "rate", "dsc"))
                                .withRel("userComments")
                ))
                .collect(Collectors.toList());
        CollectionModel<EntityModel<GameObjectResponseDTO>> collection = CollectionModel.of(entityModelList,
                linkTo(methodOn(GameObjectController.class).getObjects(page, size, sortBy, sortDir)).withSelfRel());
        return new ResponseEntity<>(collection, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObject(@PathVariable int id) {
        gameObjectService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
