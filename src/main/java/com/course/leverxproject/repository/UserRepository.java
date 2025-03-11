package com.course.leverxproject.repository;

import com.course.leverxproject.entity.User;
import com.course.leverxproject.enums.GameEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN GameObject g ON g.user = u " +
            "JOIN u.roles r " +
            "WHERE g.game = :game " +
            "AND u.rating BETWEEN :minRating AND :maxRating " +
            "AND r.name = 'ROLE_SELLER'")
    Page<User> findAllByGame(@Param("game") GameEnum game,
                             @Param("minRating") double minRating,
                             @Param("maxRating") double maxRating,
                             Pageable pageable);
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE u.rating BETWEEN :minRating AND :maxRating " +
            "AND r.name = 'ROLE_SELLER'")
    Page<User> findByRatingBetween(double minRating, double maxRating, Pageable pageable);
    Optional<User> findByEmail(String email);
}
