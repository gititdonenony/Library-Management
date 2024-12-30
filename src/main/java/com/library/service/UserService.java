package com.library.service;

import com.library.dto.UserRequest;
import com.library.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);
    UserResponse getUserById(Long userId);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long userId, UserRequest userRequest);
    void deleteUser(Long userId);
}
