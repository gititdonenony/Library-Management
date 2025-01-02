package com.library.controller;

import com.library.dto.AuthRequest;
import com.library.dto.UserRequest;
import com.library.dto.UserResponse;
import com.library.service.JwtService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Public endpoint for creating a new user


    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        System.out.println("Attempting to authenticate user: " + authRequest.getUsername());
        Authentication authentication = null;
        try {
            // Attempt authentication
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Check if authentication is successful
            if (authentication.isAuthenticated()) {
                System.out.println("User authenticated successfully: " + authentication.getName());
                return jwtService.generateToken(authRequest.getUsername());
            } else {
                System.out.println("User authentication failed: " + authRequest.getUsername());
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (Exception e) {
            // Print the exception message if authentication fails
            System.out.println("Authentication failed with error: " + e.getMessage());
            throw e;
        }
    }
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
