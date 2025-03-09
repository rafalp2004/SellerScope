package com.course.leverxproject.controller;

import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseDTO;
import com.course.leverxproject.dto.comment.CommentUpdateRequestDTO;
import com.course.leverxproject.dto.comment.SellerAndCommentDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.comment.CommentService;
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
@RequestMapping("/users")
public class CommentController {

    private final CommentService commentService;
    private final AuthService authService;

    public CommentController(CommentService commentService, AuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    @PostMapping("{userId}/comments")
    public ResponseEntity<EntityModel<CommentResponseDTO>> createComment(
            @PathVariable int userId, @RequestBody CommentCreateRequestDTO commentDTO) {
        CommentResponseDTO responseDTO = commentService.createComment(userId, commentDTO);
        EntityModel<CommentResponseDTO> entityModel = EntityModel.of(
                responseDTO,
                linkTo(methodOn(CommentController.class).getComment(responseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(userId)).withRel("user"),
                linkTo(methodOn(CommentController.class)
                        .getComments(userId, 0, 10, "rate", "dsc"))
                        .withRel("userComments")
        );
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @PutMapping("comments/{commentId}")
    public ResponseEntity<EntityModel<CommentResponseDTO>> updateComment(
            @PathVariable int commentId,
            @RequestBody CommentUpdateRequestDTO commentDTO) {
        CommentResponseDTO responseDTO = commentService.updateComment(commentId, commentDTO);
        EntityModel<CommentResponseDTO> entityModel = EntityModel.of(
                responseDTO,
                linkTo(methodOn(CommentController.class).getComment(responseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(responseDTO.sellerId())).withRel("user"),
                linkTo(methodOn(CommentController.class)
                        .getComments(responseDTO.sellerId(), 0, 10, "rate", "dsc"))
                        .withRel("sellerComments")
        );
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    //TODO Only the author can delete
    @DeleteMapping("comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable int commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<EntityModel<CommentResponseDTO>> getComment(
            @PathVariable int commentId) {
        CommentResponseDTO responseDTO = commentService.getComment(commentId);

        EntityModel<CommentResponseDTO> entityModel = EntityModel.of(
                responseDTO,
                linkTo(methodOn(CommentController.class).getComment(responseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(responseDTO.sellerId())).withRel("user"),
                linkTo(methodOn(CommentController.class)
                        .getComments(responseDTO.sellerId(), 0, 10, "rate", "dsc"))
                        .withRel("sellerComments")
        );
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @GetMapping("{userId}/comments")
    public ResponseEntity<CollectionModel<EntityModel<CommentResponseDTO>>> getComments(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rate") String sortBy,
            @RequestParam(defaultValue = "dsc") String sortDir) {
        List<CommentResponseDTO> responseDTO = commentService.getComments(userId, page, size, sortBy, sortDir);
        List<EntityModel<CommentResponseDTO>> entityModelList = responseDTO.stream()
                .map(comment -> EntityModel.of(comment,
                        linkTo(methodOn(CommentController.class).getComment(comment.id())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getSeller(userId)).withRel("user")
                ))
                .collect(Collectors.toList());
        CollectionModel<EntityModel<CommentResponseDTO>> collection = CollectionModel.of(entityModelList,
                linkTo(methodOn(CommentController.class)
                        .getComments(userId, page, size, sortBy, sortDir))
                        .withSelfRel());
        return new ResponseEntity<>(collection, HttpStatus.OK);

    }

    //TODO only for admins
    @PutMapping("/comments/{commentId}/approve")
    public ResponseEntity<Void> approveComment(@PathVariable int commentId) {
        commentService.approveComment(commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Endpoint for creating user in case when createComment throw SellerNotFoundException
    @PostMapping("/comments/seller")
    public ResponseEntity<EntityModel<CommentResponseDTO>> createCommentAndSeller(
            @RequestBody SellerAndCommentDTO sellerAndCommentDTO
    ) {
        UserResponseDTO userResponseDTO = authService.createSeller(sellerAndCommentDTO.sellerDTO());
        CommentResponseDTO responseDTO = commentService.createComment(userResponseDTO.id(), sellerAndCommentDTO.commentDTO());
        EntityModel<CommentResponseDTO> resource = EntityModel.of(responseDTO,
                linkTo(methodOn(CommentController.class).getComment(responseDTO.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getSeller(userResponseDTO.id())).withRel("user"),
                linkTo(methodOn(CommentController.class)
                        .getComments(userResponseDTO.id(), 0, 10, "rate", "dsc"))
                        .withRel("userComments")
        );
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }


}
