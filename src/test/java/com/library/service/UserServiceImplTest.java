package com.library.service;

import com.library.dto.UserRequest;
import com.library.dto.UserResponse;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        // Initialize user entity
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRoles("USER");

        // Initialize UserRequest
        userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password123");
        userRequest.setEmail("test@example.com");
        userRequest.setRole("USER");
    }

    @Test
    void testCreateUser() {
        // Mock password encoding and repository save
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Execute service method
        UserResponse response = userService.createUser(userRequest);

        // Assertions
        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));    }

    @Test
    void testGetUserById() {
        // Mock repository findById
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Execute service method
        UserResponse response = userService.getUserById(1L);

        // Assertions
        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Mock repository findById to return empty
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Assertions
        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllUsers() {
        // Mock repository findAll
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        // Execute service method
        List<UserResponse> users = userService.getAllUsers();

        // Assertions
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUser() {
        // Mock repository findById and save
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Execute service method
        UserResponse response = userService.updateUser(1L, userRequest);

        // Assertions
        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Mock repository findById
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Execute service method
        userService.deleteUser(1L);

        // Verify repository delete is called
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        // Mock repository findById to return empty
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Assertions
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
}