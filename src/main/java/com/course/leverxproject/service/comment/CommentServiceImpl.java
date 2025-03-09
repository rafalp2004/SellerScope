package com.course.leverxproject.service.comment;

import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseDTO;
import com.course.leverxproject.dto.comment.CommentUpdateRequestDTO;
import com.course.leverxproject.entity.Comment;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.comment.CommentNotFoundException;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.CommentRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final AuthService authService;

    private final UserService userService;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, AuthService authService, UserService userService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;

        this.authService = authService;
        this.userService = userService;
    }


    @Override
    public CommentResponseDTO createComment(int userId, CommentCreateRequestDTO commentDTO) {
        //TODO Add checking if seller has role Seller.
        User seller = userRepository.findById(userId).orElseThrow(() -> new SellerNotFoundException("Seller with id " + userId + " not found"));
        //TODO Consider what to do to set this user in session (Dont create users every time in one session)
        User author = authService.createAnonymous();

        Comment comment = new Comment(
                commentDTO.message(),
                commentDTO.rate(),
                author,
                seller,
                LocalDateTime.now(),
                false
        );

        commentRepository.save(comment);
        return new CommentResponseDTO(
                comment.getMessage(),
                comment.getRate(),
                "Anonymous",
                comment.getSeller().getFirstName() + " " + comment.getSeller().getLastName(),
                comment.getCreatedAt(),
                comment.getApproved()
        );
    }

    @Override
    public CommentResponseDTO updateComment(int commentId, CommentUpdateRequestDTO commentDTO) {
        //TODO Add validation (only author of comment cant comment)
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));
        comment.setMessage(commentDTO.message());
        comment.setRate(commentDTO.rate());
        commentRepository.save(comment);

        return new CommentResponseDTO(
                comment.getMessage(),
                comment.getRate(),
                comment.getAuthor().getFirstName(),
                comment.getSeller().getFirstName() + " " + comment.getSeller().getLastName(),
                comment.getCreatedAt(),
                comment.getApproved()
        );
    }

    @Override
    public void deleteComment(int commentId) {
        //TODO Check if user has permission to do that.
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));
        commentRepository.delete(comment);
    }

    @Override
    public CommentResponseDTO getComment(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));

        return new CommentResponseDTO(
                comment.getMessage(),
                comment.getRate(),
                comment.getAuthor().getFirstName(),
                comment.getSeller().getFirstName() + " " + comment.getSeller().getLastName(),
                comment.getCreatedAt(),
                comment.getApproved()
        );
    }

    @Override
    public List<CommentResponseDTO> getComments(int userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findAllBySellerId(userId, pageable);
        return comments
                .stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getMessage(),
                        comment.getRate(),
                        comment.getAuthor().getFirstName(),
                        comment.getSeller().getFirstName() + " " + comment.getSeller().getLastName(),
                        comment.getCreatedAt(),
                        comment.getApproved()

                )).toList();
    }

    @Override
    public void approveComment(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));

        comment.setApproved(true);
        commentRepository.save(comment);
        userService.updateAverage(comment.getSeller().getId());

    }
}
