package com.course.leverxproject.controller;


import com.course.leverxproject.dto.gameObject.GameObjectCreateRequestDTO;
import com.course.leverxproject.dto.gameObject.GameObjectResponseDTO;
import com.course.leverxproject.dto.gameObject.GameObjectUpdateRequestDTO;
import com.course.leverxproject.service.gameObject.GameObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/objects")
public class GameObjectController {
    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @PostMapping
    private ResponseEntity<GameObjectResponseDTO> createObject(@RequestBody GameObjectCreateRequestDTO gameDTO){
        GameObjectResponseDTO responseDTO = gameObjectService.create(gameDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    private ResponseEntity<GameObjectResponseDTO> updateObject(@PathVariable int id , @RequestBody GameObjectUpdateRequestDTO gameDTO){
        GameObjectResponseDTO responseDTO = gameObjectService.update(id,gameDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    private ResponseEntity<GameObjectResponseDTO> getObject(@PathVariable int id ){
        GameObjectResponseDTO responseDTO = gameObjectService.getById(id);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping
    private ResponseEntity<List<GameObjectResponseDTO>> getObjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "dsc") String sortDir
    ){
        List<GameObjectResponseDTO> responseDTO = gameObjectService.getAll(page, size, sortBy, sortDir);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteObject(@PathVariable int id ){
        gameObjectService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
