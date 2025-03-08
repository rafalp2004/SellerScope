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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

//TODO add validation
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
        // TODO - Get id form session.
        int id = 5;
        User user = userRepository.findById(id).orElseThrow(() -> new SellerNotFoundException("Seller with id " + id + " not found"));
        GameObject gameObject = new GameObject(
                gameDTO.title(),
                gameDTO.text(),
                gameDTO.game(),
                user,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        gameObjectRepository.save(gameObject);
        return new GameObjectResponseDTO(
                gameObject.getTitle(),
                gameObject.getText(),
                gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName(),
                gameObject.getCreatedAt(),
                gameObject.getUpdatedAt()
        );
    }

    @Override
    public GameObjectResponseDTO update(int id, GameObjectUpdateRequestDTO gameDTO) {
        GameObject gameObject = gameObjectRepository.findById(id).orElseThrow(() -> new GameObjectNotFoundException("Game object with id " + id + " not found"));
        gameObject.setTitle(gameDTO.title());
        gameObject.setText(gameDTO.text());
        gameObject.setGame(gameDTO.game());
        gameObject.setUpdatedAt(LocalDateTime.now());
        gameObjectRepository.save(gameObject);
        return new GameObjectResponseDTO(
                gameObject.getTitle(),
                gameObject.getText(),
                gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName(),
                gameObject.getCreatedAt(),
                gameObject.getUpdatedAt()
        );
    }

    @Override
    public GameObjectResponseDTO getById(int id) {
        GameObject gameObject = gameObjectRepository.findById(id).orElseThrow(() -> new GameObjectNotFoundException("Game object with id " + id + " not found"));
        return new GameObjectResponseDTO(
                gameObject.getTitle(),
                gameObject.getText(),
                gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName(),
                gameObject.getCreatedAt(),
                gameObject.getUpdatedAt()
        );
    }

    @Override
    public List<GameObjectResponseDTO> getAll() {
        List<GameObject> gameObjectList = gameObjectRepository.findAll();
        return gameObjectList.stream().map(gameObject -> new GameObjectResponseDTO(
                gameObject.getTitle(),
                gameObject.getText(),
                gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName(),
                gameObject.getCreatedAt(),
                gameObject.getUpdatedAt()
        )).toList();
    }

    @Override
    public void deleteById(int id) {
        GameObject gameObject = gameObjectRepository.findById(id).orElseThrow(() -> new GameObjectNotFoundException("Game object with id " + id + " not found"));
        gameObjectRepository.delete(gameObject);
    }
}
