package com.course.leverxproject.service.myUser;

import com.course.leverxproject.entity.User;
import com.course.leverxproject.repository.UserRepository;
import com.course.leverxproject.entity.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements CustomUserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found."));
        return new MyUserDetails(user);
    }

    @Override
    public UserDetails loadByUserId(String id) {
        User user = userRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found."));
        return new MyUserDetails(user);
    }

}
