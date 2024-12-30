package com.library.service;

import com.library.dto.UserRequest;
import com.library.dto.UserResponse;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        try {
            User user = modelMapper.map(userRequest, User.class);
            user = userRepository.save(user);
            return modelMapper.map(user, UserResponse.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating user: " + ex.getMessage());
        }
    }

    @Override
    public UserResponse getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
            return modelMapper.map(user, UserResponse.class);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving user: " + ex.getMessage());
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(user -> modelMapper.map(user, UserResponse.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving users: " + ex.getMessage());
        }
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

            modelMapper.map(userRequest, existingUser);
            existingUser = userRepository.save(existingUser);

            return modelMapper.map(existingUser, UserResponse.class);
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

