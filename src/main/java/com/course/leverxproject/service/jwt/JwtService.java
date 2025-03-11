package com.course.leverxproject.service.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String email);

    String generateToken(String email, String userType);

    String extractSubject(String token);

    String extractUserType(String token);


    boolean validateToken(String token, UserDetails userDetails);

    Authentication getAuthentication(String token);
}