package com.course.leverxproject.utils;

import com.course.leverxproject.service.auth.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class SecurityUtils {
    public static Optional<MyUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof MyUserDetails) {
            log.info(((MyUserDetails) principal).getUser().toString());
            return Optional.of((MyUserDetails) principal);
        }
        return Optional.empty();
    }
}
