package com.course.leverxproject.service.auth;

import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.entity.Role;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.role.RoleNotFoundException;
import com.course.leverxproject.repository.RoleRepository;
import com.course.leverxproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponseDTO createSeller(UserCreateRequestDTO userDTO) {

        Role role = roleRepository.findByName("ROLE_SELLER").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        User seller = new User(
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.password(),
                userDTO.email(),
                LocalDateTime.now(),
                Set.of(role)
        );
        userRepository.save(seller);

        return new UserResponseDTO(seller.getFirstName(), seller.getLastName(), seller.getEmail(), LocalDateTime.now());
    }
}
