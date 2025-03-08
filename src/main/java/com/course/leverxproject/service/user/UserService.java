package com.course.leverxproject.service.user;

import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.enums.GameEnum;

import java.util.List;

public interface UserService {
    void updateAverage(int userId);
    List<UserResponseDTO> findAll(int page, int size, String sortBy, String sortDir, GameEnum gameFilter, double minRating, double maxRating);

    UserResponseDTO findById(int userId);
}
