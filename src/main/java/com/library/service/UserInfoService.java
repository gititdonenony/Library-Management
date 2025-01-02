package com.library.service;

import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.security.UserInfoDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepository.findByUsername(username);
        if (userDetail.isEmpty()) {
             throw new UsernameNotFoundException("User not found: " + username);
        }
        User user = userDetail.get();
        return new UserInfoDetails(user);
    }
}
