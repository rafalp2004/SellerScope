package com.course.leverxproject.repository;

import com.course.leverxproject.entity.GameObject;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.enums.GameEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
}
