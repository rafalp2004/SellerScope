package com.course.leverxproject.service.auth;

import com.course.leverxproject.dto.user.LoginRequestDTO;
import com.course.leverxproject.dto.user.LoginResponseDTO;
import com.course.leverxproject.dto.user.UserCreateRequestDTO;
import com.course.leverxproject.dto.user.UserResponseDTO;
import com.course.leverxproject.entity.Role;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.role.RoleNotFoundException;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.RoleRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.service.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponseDTO createSeller(UserCreateRequestDTO userDTO) {

        Role role = roleRepository.findByName("ROLE_SELLER").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        User seller = new User(
                userDTO.firstName(),
                userDTO.lastName(),
                encoder.encode(userDTO.password()),
                userDTO.email(),
                LocalDateTime.now(),
                Set.of(role)
        );
        userRepository.save(seller);

        return new UserResponseDTO(seller.getId(), seller.getFirstName(), seller.getLastName(), seller.getEmail(), LocalDateTime.now());
    }

    @Override
    public User createAnonymous() {
        Role role = roleRepository.findByName("ROLE_ANONYMOUS").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        User anonymousUser = new User(
                "Anonymous",
                "",
                "",
                "",
                LocalDateTime.now(),
                Set.of(role)

        );
        userRepository.save(anonymousUser);
        return anonymousUser;
    }

    @Override
    public void approveSeller(int userId) {
        User seller = userRepository.findById(userId).orElseThrow(() -> new SellerNotFoundException("Seller with id " + userId + " not found"));
        seller.setApproved(true);
        userRepository.save(seller);
    }

    @Override
    public LoginResponseDTO verify(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.email()).orElseThrow(() -> new SellerNotFoundException("User with email " + loginRequestDTO.email() + " not found"));

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        if(authentication.isAuthenticated()){
            return new LoginResponseDTO(
                    jwtService.generateToken(user.getEmail()),
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getCreatedAt()
            );
        } else {
            throw new AuthenticationCredentialsNotFoundException("Credentials are incorrect");
        }
    }


}
