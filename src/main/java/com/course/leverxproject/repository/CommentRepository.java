package com.course.leverxproject.repository;

import com.course.leverxproject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllBySellerId(int sellerId);
}
