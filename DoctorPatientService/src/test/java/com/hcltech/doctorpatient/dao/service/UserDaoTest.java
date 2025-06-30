package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
import com.hcltech.doctorpatient.model.Role;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDaoTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userDao = new UserDao(passwordEncoder, userRepository);
    }

    @Test
    void testCreateUser_Success() {
        User user = new User();
        user.setPassword("plain");
        User savedUser = new User();
        savedUser.setPassword("encoded");

        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userDao.createUser(user);

        assertEquals("encoded", user.getPassword());
        assertEquals(savedUser, result);
        verify(userRepository).save(user);
    }

    @Test
    void testGetAllUsers_ReturnsList() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userDao.getAllUsers();

        assertEquals(1, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void testDeleteUser_UserExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> userDao.deleteUser(id));
        verify(userRepository).deleteById(id);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userDao.deleteUser(id));
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void testUpdateUser_Success() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        User updated = new User();
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setMobile("1111111111");
        updated.setRole(Role.DOCTOR);

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userDao.updateUser(updated, id);

        assertEquals("New", existing.getFirstName());
        assertEquals(Role.DOCTOR, existing.getRole());
        assertEquals(existing, result);
        verify(userRepository).save(existing);
    }

    @Test
    void testUpdateUser_NotFound() {
        UUID id = UUID.randomUUID();
        User updated = new User();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userDao.updateUser(updated, id));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserById_Success() {
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userDao.getUserById(id);

        assertEquals(user, result);
    }

    @Test
    void testGetUserById_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userDao.getUserById(id));
    }
}