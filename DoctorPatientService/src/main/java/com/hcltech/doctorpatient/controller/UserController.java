package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dto.UserDTO;
import com.hcltech.doctorpatient.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Registering new user: {}", userDTO.getMobile());
        UserDTO createdUser = userService.createUser(userDTO);
        log.info("User registered successfully with ID: {}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','DOCTOR')")
    @PutMapping("/{uid}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable UUID uid) {
        log.info("Updating user with ID: {}", uid);
        UserDTO updatedUser = userService.updateUser(userDTO, uid);
        log.info("User updated successfully: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<UserDTO> users = userService.getAllUsers();
        log.info("Total users fetched: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id) {
        log.info("Fetching user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        log.info("User fetched: {}", user);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
