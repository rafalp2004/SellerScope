package com.course.leverxproject.repository;

import com.course.leverxproject.entity.User;
import com.course.leverxproject.enums.GameEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT DISTINCT u FROM User u JOIN GameObject g ON g.user = u " +
            "WHERE g.game = :game AND u.rating BETWEEN :minRating AND :maxRating")
    Page<User> findAllByGame(@Param("game") GameEnum game,
                             @Param("minRating") double minRating,
                             @Param("maxRating") double maxRating,
                             Pageable pageable);

    Page<User> findByRatingBetween(double minRating, double maxRating, Pageable pageable);

}
