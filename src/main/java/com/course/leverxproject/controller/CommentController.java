package com.course.leverxproject.controller;

import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseDTO;
import com.course.leverxproject.dto.comment.CommentUpdateRequestDTO;
import com.course.leverxproject.dto.comment.SellerAndCommentDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.comment.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable int userId, @RequestBody CommentCreateRequestDTO commentDTO) {
        CommentResponseDTO responseDTO = commentService.createComment(userId, commentDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("comments/{commentId}")
    private ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable int commentId,
            @RequestBody CommentUpdateRequestDTO commentDTO) {
        CommentResponseDTO responseDTO = commentService.updateComment(commentId, commentDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    //TODO Only the author can delete
    @DeleteMapping("comments/{commentId}")
    private ResponseEntity<Void> deleteComment(
            @PathVariable int commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comments/{commentId}")
    private ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable int commentId) {
        CommentResponseDTO responseDTO = commentService.getComment(commentId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("{userId}/comments")
    private ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rate") String sortBy,
            @RequestParam(defaultValue = "dsc") String sortDir) {
        List<CommentResponseDTO> responseDTO = commentService.getComments(userId, page, size, sortBy, sortDir);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    //TODO only for admins
    @PutMapping("/comments/{commentId}/approve")
    private ResponseEntity<Void> approveComment(@PathVariable int commentId) {
        commentService.approveComment(commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Endpoint for creating user in case when createComment throw SellerNotFoundException
    @PostMapping("/comments/seller")
    private ResponseEntity<CommentResponseDTO> createCommentAndSeller(
            @RequestBody SellerAndCommentDTO sellerAndCommentDTO
    ) {
        UserResponseDTO userResponseDTO = authService.createSeller(sellerAndCommentDTO.sellerDTO());
        CommentResponseDTO responseDTO = commentService.createComment(userResponseDTO.id(), sellerAndCommentDTO.commentDTO());
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


}
