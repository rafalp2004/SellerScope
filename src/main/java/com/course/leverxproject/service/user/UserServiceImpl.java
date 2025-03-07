package com.course.leverxproject.service.user;

import com.course.leverxproject.entity.Comment;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.CommentRepository;
import com.course.leverxproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public UserServiceImpl(UserRepository userRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void updateAverage(int userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new SellerNotFoundException("Seller with id " + userId + " not found."));
        List<Comment> comments = commentRepository.findAllBySellerId(userId);
        double avg = comments.stream().mapToInt(Comment::getRate).average().getAsDouble();
        user.setRating(avg);
        userRepository.save(user);
    }
}
