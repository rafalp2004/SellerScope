package com.course.leverxproject.repository;

import com.course.leverxproject.entity.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
}
