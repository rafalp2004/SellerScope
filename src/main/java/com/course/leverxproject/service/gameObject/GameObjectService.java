package com.course.leverxproject.service.gameObject;


import com.course.leverxproject.dto.gameObject.GameObjectCreateRequestDTO;
import com.course.leverxproject.dto.gameObject.GameObjectResponseDTO;
import com.course.leverxproject.dto.gameObject.GameObjectUpdateRequestDTO;

import java.util.List;

public interface GameObjectService {
    GameObjectResponseDTO create(GameObjectCreateRequestDTO gameDTO);

    GameObjectResponseDTO update(int id, GameObjectUpdateRequestDTO gameDTO);

    GameObjectResponseDTO getById(int id);

    void deleteById(int id);

    List<GameObjectResponseDTO> getAll(int page, int size, String sortBy, String sortDir);
}
