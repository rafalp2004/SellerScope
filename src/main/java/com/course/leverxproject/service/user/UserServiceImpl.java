package com.course.leverxproject.service.user;

import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.entity.Comment;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.enums.GameEnum;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.CommentRepository;
import com.course.leverxproject.repository.GameObjectRepository;
import com.course.leverxproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final GameObjectRepository gameObjectRepository;

    public UserServiceImpl(UserRepository userRepository, UserRepository userRepository1, CommentRepository commentRepository, GameObjectRepository gameObjectRepository) {
        this.userRepository = userRepository1;
        this.commentRepository = commentRepository;
        this.gameObjectRepository = gameObjectRepository;
    }

    @Override
    public void updateAverage(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new SellerNotFoundException("Seller with id " + userId + " not found."));
        List<Comment> comments = commentRepository.findAllBySellerId(userId);
        double avg = comments.stream().mapToInt(Comment::getRate).average().getAsDouble();
        user.setRating(avg);
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> findAll(int page, int size, String sortBy, String sortDir, GameEnum gameFilter, double minRating, double maxRating) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> sellers;
        if (gameFilter != null) {
            sellers = userRepository.findAllByGame(gameFilter, minRating, maxRating, pageable);
        } else {
            sellers = userRepository.findByRatingBetween(minRating, maxRating, pageable);
        }
        return sellers.stream()
                .map(this::userToUserResponseDTO
                ).toList();
    }

    @Override
    public UserResponseDTO findById(int userId) {
        User seller = userRepository.findById(userId).orElseThrow(() -> new SellerNotFoundException("Seller with id " + userId + " not found"));

        return userToUserResponseDTO(seller);
    }

    private UserResponseDTO userToUserResponseDTO(User seller) {
        return new UserResponseDTO(
                seller.getId(),
                seller.getFirstName(),
                seller.getLastName(),
                seller.getEmail(),
                seller.getCreatedAt()
        );
    }

}
