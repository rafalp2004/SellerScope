package com.course.leverxproject.service.auth;

import com.course.leverxproject.dto.user.*;
import com.course.leverxproject.entity.Role;
import com.course.leverxproject.entity.User;
import com.course.leverxproject.exception.role.RoleNotFoundException;
import com.course.leverxproject.exception.user.SellerNotEnabledException;
import com.course.leverxproject.exception.user.SellerNotFoundException;
import com.course.leverxproject.repository.RoleRepository;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.service.jwt.JwtService;
import com.course.leverxproject.service.mail.EmailService;
import com.course.leverxproject.service.redis.RedisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RedisServiceImpl redisService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Value("${MAIL_USERNAME}")
    private String mailFrom;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService, RedisServiceImpl redisService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.redisService = redisService;
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
                false,
                false,
                Set.of(role)
        );
        seller.setEnabled(false);
        userRepository.save(seller);

        String verificationCode = UUID.randomUUID().toString();
        redisService.saveVerificationCode(seller.getEmail(), verificationCode);
        String verificationLink = "http://localhost:8080/auth/verify?email=" + seller.getEmail() + "&code=" + verificationCode;
        emailService.sendMail(
                "Verification",
                "Click this link to verify your account: " + verificationLink,
                seller.getEmail(),
                mailFrom
        );
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
                false,
                false,
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
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.email()).orElseThrow(() -> new SellerNotFoundException("User with email " + loginRequestDTO.email() + " not found"));
        if (!user.getEnabled()) {
            throw new SellerNotEnabledException("Seller not enabled");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        if (authentication.isAuthenticated()) {
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

    @Override
    public UserResponseDTO verifyAccount(String email, String code) {
        String storedCode = redisService.getVerificationCode(email);
        if (storedCode != null && storedCode.equals(code)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new SellerNotFoundException("User with email " + email + " not found"));
            user.setEnabled(true);
            userRepository.save(user);
            redisService.removeVerificationCode(email);
            return new UserResponseDTO(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getCreatedAt()
            );
        }
        return null;
    }

    @Override
    public void forgotPassword(String email) {
        String verificationCode = UUID.randomUUID().toString();
        redisService.saveVerificationCode(email + "pr", verificationCode);//Adding pr because we already use just email
        emailService.sendMail(
                "Recovery password",
                "Your code for recovery password: " + verificationCode,
                email,
                mailFrom
        );
    }

    @Override
    public UserResponseDTO resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String storedCode = redisService.getVerificationCode(resetPasswordDTO.email() + "pr");
        if (storedCode != null && storedCode.equals(resetPasswordDTO.code())) {
            User user = userRepository.findByEmail(resetPasswordDTO.email()).orElseThrow(() -> new SellerNotFoundException("User with email " + resetPasswordDTO.email() + " not found"));
            user.setPassword(encoder.encode(resetPasswordDTO.newPassword()));
            userRepository.save(user);
            redisService.removeVerificationCode(resetPasswordDTO.email() + "pr");
            return new UserResponseDTO(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getCreatedAt()
            );
        }
        return null;
    }


}
