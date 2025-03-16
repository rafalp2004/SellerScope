package com.course.leverxproject.service.gameObject;

import com.course.leverxproject.dto.gameObject.GameObjectCreateRequestDTO;
import com.course.leverxproject.dto.gameObject.GameObjectResponseDTO;
import com.course.leverxproject.dto.gameObject.GameObjectUpdateRequestDTO;
import com.course.leverxproject.entity.GameObject;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.gameobject.GameObjectNotFoundException;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.GameObjectRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.entity.MyUserDetails;
import com.course.leverxproject.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameObjectServiceImpl implements GameObjectService {
    private final GameObjectRepository gameObjectRepository;
    private final UserRepository userRepository;

    public GameObjectServiceImpl(GameObjectRepository gameObjectRepository, UserRepository userRepository) {
        this.gameObjectRepository = gameObjectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GameObjectResponseDTO create(GameObjectCreateRequestDTO gameDTO) {

        int currentUserId = SecurityUtils.getCurrentUser().map(MyUserDetails::getId).orElseThrow(() -> new AccessDeniedException("User not authenticated."));
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new SellerNotFoundException("Seller with id " + currentUserId + " not found"));
        GameObject gameObject = new GameObject(
                gameDTO.title(),
                gameDTO.text(),
                gameDTO.game(),
                user,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        gameObjectRepository.save(gameObject);
        return gameObjectToGameObjectResponseDTO(gameObject);
    }

    @Override
    public GameObjectResponseDTO update(int id, GameObjectUpdateRequestDTO gameDTO) {
        int currentUserId = SecurityUtils.getCurrentUser().map(MyUserDetails::getId).orElseThrow(() -> new AccessDeniedException("User not authenticated."));
        GameObject gameObject = gameObjectRepository.findById(id).orElseThrow(() -> new GameObjectNotFoundException("Game object with id " + id + " not found"));

        if (gameObject.getUser().getId() != currentUserId) {
            throw new AccessDeniedException("User not authorized to update object.");
        }

        gameObject.setTitle(gameDTO.title());
        gameObject.setText(gameDTO.text());
        gameObject.setGame(gameDTO.game());
        gameObject.setUpdatedAt(LocalDateTime.now());
        gameObjectRepository.save(gameObject);
        return gameObjectToGameObjectResponseDTO(gameObject);
    }

    @Override
    public GameObjectResponseDTO getById(int id) {
        GameObject gameObject = gameObjectRepository.findById(id).orElseThrow(() -> new GameObjectNotFoundException("Game object with id " + id + " not found"));
        return gameObjectToGameObjectResponseDTO(gameObject);
    }

    @Override
    public List<GameObjectResponseDTO> getAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GameObject> gameObjectList = gameObjectRepository.findAll(pageable);
        return gameObjectList.stream().map(this::gameObjectToGameObjectResponseDTO).toList();
    }

    @Override
    public void deleteById(int id) {
        int currentUserId = SecurityUtils.getCurrentUser().map(MyUserDetails::getId).orElseThrow(() -> new AccessDeniedException("User not authenticated."));
        GameObject gameObject = gameObjectRepository.findById(id).orElseThrow(() -> new GameObjectNotFoundException("Game object with id " + id + " not found"));

        if (gameObject.getUser().getId() != currentUserId) {
            throw new AccessDeniedException("User not authorized to update object.");
        }
        gameObjectRepository.delete(gameObject);
    }

    private GameObjectResponseDTO gameObjectToGameObjectResponseDTO(GameObject gameObject) {
        return new GameObjectResponseDTO(
                gameObject.getId(),
                gameObject.getTitle(),
                gameObject.getText(),
                gameObject.getUser().getId(),
                gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName(),
                gameObject.getCreatedAt(),
                gameObject.getUpdatedAt()
        );
    }
}
