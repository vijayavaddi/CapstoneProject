package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.UserDao;
import com.hcltech.doctorpatient.dto.UserDTO;
import com.hcltech.doctorpatient.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDAO;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User();
        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setMobile("1234567890");
        userDTO.setRole("PATIENT");
    }

    @Test
    void testCreateUser() {
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(userDAO.createUser(user)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userDAO).createUser(user);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(user);
        when(userDAO.getAllUsers()).thenReturn(users);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(userDAO).getAllUsers();
    }

    @Test
    void testGetUserById() {
        when(userDAO.getUserById(userId)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userDAO).getUserById(userId);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userDAO).deleteUser(userId);

        userService.deleteUser(userId);

        verify(userDAO).deleteUser(userId);
    }

    @Test
    void testUpdateUser() {
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(userDAO.updateUser(user, userId)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.updateUser(userDTO, userId);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userDAO).updateUser(user, userId);
    }
}