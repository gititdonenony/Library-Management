package com.library.service;

import com.library.dto.UserRequest;
import com.library.dto.UserResponse;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        try {
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setPassword(userRequest.getPassword()); // Hash the password in real-world apps
            user.setEmail(userRequest.getEmail());
            user.setRoles(userRequest.getRole());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = userRepository.save(user);
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());        } catch (Exception ex) {
            throw new RuntimeException("Error creating user: " + ex.getMessage());
        }
    }

    @Override
    public UserResponse getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving user: " + ex.getMessage());
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving users: " + ex.getMessage());
        }
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        try {
            // Retrieve the existing user
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

            // Manually copy fields from UserRequest to existingUser
            existingUser.setUsername(userRequest.getUsername());
            existingUser.setPassword(userRequest.getPassword()); // Hash the password in a real-world app
            existingUser.setEmail(userRequest.getEmail());

            existingUser.setRoles(userRequest.getRole());
            // Save the updated user
            existingUser = userRepository.save(existingUser);

            // Create and return a UserResponse manually
            return new UserResponse(existingUser.getId(), existingUser.getUsername(), existingUser.getEmail(), existingUser.getRoles());
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Error updating user: " + ex.getMessage());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
            userRepository.delete(user);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting user: " + ex.getMessage());
        }
    }
}
