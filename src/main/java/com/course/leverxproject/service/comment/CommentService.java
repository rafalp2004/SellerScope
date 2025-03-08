package com.course.leverxproject.service.comment;

import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseDTO;
import com.course.leverxproject.dto.comment.CommentUpdateRequestDTO;

import java.util.List;

public interface CommentService {

    CommentResponseDTO createComment(int userId, CommentCreateRequestDTO commentDTO);

    CommentResponseDTO updateComment(int commentId, CommentUpdateRequestDTO commentDTO);

    void deleteComment(int commentId);

    CommentResponseDTO getComment(int commentId);

    List<CommentResponseDTO> getComments(int userId, int page, int size, String sortBy, String sortDir);

    void approveComment(int commentId);
}
