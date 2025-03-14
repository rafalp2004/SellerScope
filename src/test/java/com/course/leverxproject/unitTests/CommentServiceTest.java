package com.course.leverxproject.unitTests;

import com.course.leverxproject.dto.comment.CommentCreateRequestDTO;
import com.course.leverxproject.dto.comment.CommentResponseWithTokenDTO;
import com.course.leverxproject.entity.Comment;
import com.course.leverxproject.entity.Role;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.repository.CommentRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.service.auth.AuthService;
import com.course.leverxproject.service.auth.MyUserDetails;
import com.course.leverxproject.service.comment.CommentServiceImpl;
import com.course.leverxproject.service.jwt.JwtService;
import com.course.leverxproject.service.user.UserService;
import com.course.leverxproject.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private MockedStatic<SecurityUtils> securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityUtils = Mockito.mockStatic(SecurityUtils.class);

    }

    @AfterEach
    void closeMocks() {
        securityUtils.close();
    }

    @Test
    void testCreateComment() {
        //Arrange
        int sellerId = 1;
        int authorId = 2;
        CommentCreateRequestDTO commentCreateRequestDTO = new CommentCreateRequestDTO(
                "good seller", Short.parseShort("1")
        );

        Role sellerRole = new Role("ROLE_SELLER");
        User seller = new User();
        seller.setId(sellerId);
        seller.setFirstName("Jan");
        seller.setLastName("Kowalski");
        seller.setRoles(Set.of(sellerRole));

        User author = new User();
        author.setId(authorId);
        author.setFirstName("Adam");

        MyUserDetails userDetails = new MyUserDetails(author);
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(Optional.of(userDetails));

        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        String token = "token";
        when(jwtService.generateToken(anyString(), anyString())).thenReturn(token);
        when(jwtService.getAuthentication(token)).thenReturn(mock(Authentication.class));


        //ACT
        CommentResponseWithTokenDTO response = commentService.createComment(sellerId, commentCreateRequestDTO);

        //ASSERT
        verify(commentRepository).save(any(Comment.class));
        assertNotNull(response);
        assertEquals(token, response.token());
        assertEquals("good seller", response.commentResponseDTO().message());


    }
    @Test
    void testApproveComment() {
        // Arrange
        int commentId = 1;
        int sellerId = 100;

        User seller = new User();
        seller.setId(sellerId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setApproved(false);
        comment.setSeller(seller);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.approveComment(commentId);

        // Assert
        verify(commentRepository).save(comment);
        verify(userService).updateAverage(sellerId);
        assertTrue(comment.getApproved());
    }


}
