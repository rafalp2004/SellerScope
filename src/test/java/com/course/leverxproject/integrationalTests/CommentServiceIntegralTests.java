package com.course.leverxproject.integrationalTests;


import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseWithTokenDTO;
import com.course.leverxproject.entity.Comment;
import com.course.leverxproject.entity.Role;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.repository.CommentRepository;
import com.course.leverxproject.repository.RoleRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommentServiceIntegralTests {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Test
    void testCreateCommentIntegration() {

        // Arrange
        Role sellerRole = new Role("ROLE_SELLER");
        Role anonymousRole = new Role("ROLE_ANONYMOUS");

        roleRepository.save(sellerRole);

        roleRepository.save(anonymousRole);
        User seller = new User();
        seller.setFirstName("Jan");
        seller.setLastName("Kowalski");
        seller.setRoles(Set.of(sellerRole));
        seller = userRepository.save(seller);

        CommentCreateRequestDTO commentDTO = new CommentCreateRequestDTO("Test comment integration", (short) 5);

        // Act
        CommentResponseWithTokenDTO response = commentService.createComment(seller.getId(), commentDTO);

        // Assert
        assertNotNull(response);
        Optional<Comment> savedComment = commentRepository.findById(response.commentResponseDTO().id());
        assertTrue(savedComment.isPresent());
        assertEquals("Test comment integration", savedComment.get().getMessage());
    }

    @Test
    void testApproveCommentIntegration() {

        // Arrange
        Role sellerRole = new Role("ROLE_SELLER");
        sellerRole = roleRepository.save(sellerRole);

        User seller = new User();
        seller.setFirstName("Jan");
        seller.setLastName("Kowalski");
        seller.setRoles(new HashSet<>(Collections.singleton(sellerRole)));
        seller = userRepository.save(seller);

        Comment comment = new Comment("Integration comment", (short) 3, null, seller, LocalDateTime.now(), false);
        comment = commentRepository.save(comment);

        // Act
        commentService.approveComment(comment.getId());

        // Assert
        Comment updatedComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        assertTrue(updatedComment.getApproved());

    }
}

