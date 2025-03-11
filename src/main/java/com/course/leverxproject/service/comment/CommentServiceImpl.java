package com.course.leverxproject.service.comment;

import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseDTO;
import com.course.leverxproject.dto.comment.CommentResponseWithTokenDTO;
import com.course.leverxproject.dto.comment.CommentUpdateRequestDTO;
import com.course.leverxproject.entity.Comment;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.comment.CommentNotFoundException;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.CommentRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.auth.MyUserDetails;
import com.course.leverxproject.service.jwt.JwtService;
import com.course.leverxproject.service.user.UserService;
import com.course.leverxproject.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final AuthService authService;

    private final UserService userService;

    private final JwtService jwtService;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, AuthService authService, UserService userService, JwtService jwtService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;

        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @Override
    public CommentResponseWithTokenDTO createComment(int userId, CommentCreateRequestDTO commentDTO) {

        User seller = userRepository.findById(userId).orElseThrow(() -> new SellerNotFoundException("Seller with id " + userId + " not found"));
        if (seller.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_SELLER"))) {
            throw new SellerNotFoundException("Cannot find seller with " + userId + "id");
        }
        Optional<MyUserDetails> currentUser = SecurityUtils.getCurrentUser();
        User author;

        if (currentUser.isPresent()) {
            author = currentUser.get().getUser();
        } else {
            author = authService.createAnonymous();
        }

        Comment comment = new Comment(
                commentDTO.message(),
                commentDTO.rate(),
                author,
                seller,
                LocalDateTime.now(),
                false
        );

        commentRepository.save(comment);
        String token = jwtService.generateToken(String.valueOf(author.getId()), "ANONYMOUS");
        Authentication auth = jwtService.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return new CommentResponseWithTokenDTO(new CommentResponseDTO(
                comment.getId(),
                comment.getMessage(),
                comment.getRate(),
                "Anonymous",
                comment.getSeller().getId(),
                comment.getSeller().getFirstName() + " " + comment.getSeller().getLastName(),
                comment.getCreatedAt(),
                comment.getApproved()
        ), token
        );
    }

    @Override
    public CommentResponseDTO updateComment(int commentId, CommentUpdateRequestDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));

        int currentUserId = SecurityUtils.getCurrentUser().map(MyUserDetails::getId).orElseThrow(() -> new AccessDeniedException("User not authenticated"));


        if (comment.getAuthor().getId() != currentUserId) {
            throw new AccessDeniedException("User not authorized to update this comment.");

        }

        comment.setMessage(commentDTO.message());
        comment.setRate(commentDTO.rate());
        commentRepository.save(comment);

        return commentToCommentResponseDTO(comment);
    }

    @Override
    public void deleteComment(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));
        int currentUserId = SecurityUtils.getCurrentUser()
                .map(MyUserDetails::getId)
                .orElseThrow(() -> new AccessDeniedException("User not authenticated."));
        if (comment.getAuthor().getId() != currentUserId) {
            throw new AccessDeniedException("User not authorized to delete comment.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public CommentResponseDTO getComment(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));

        return commentToCommentResponseDTO(comment);
    }


    @Override
    public List<CommentResponseDTO> getComments(int userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findAllBySellerId(userId, pageable);
        return comments
                .stream()
                .map(this::commentToCommentResponseDTO
                ).toList();
    }

    @Override
    public void approveComment(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));

        comment.setApproved(true);
        commentRepository.save(comment);
        userService.updateAverage(comment.getSeller().getId());

    }


    private CommentResponseDTO commentToCommentResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getMessage(),
                comment.getRate(),
                comment.getAuthor().getFirstName(),
                comment.getSeller().getId(),
                comment.getSeller().getFirstName() + " " + comment.getSeller().getLastName(),
                comment.getCreatedAt(),
                comment.getApproved()
        );
    }
}
