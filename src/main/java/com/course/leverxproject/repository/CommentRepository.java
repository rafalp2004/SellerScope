package com.course.leverxproject.repository;

import com.course.leverxproject.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllBySellerId(int sellerId);

    Page<Comment> findAllBySellerId(int userId, Pageable pageable);
}
