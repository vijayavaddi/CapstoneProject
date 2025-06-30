package com.hcltech.doctorpatient.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.doctorpatient.config.TestSecurityConfig;
import com.hcltech.doctorpatient.dao.service.UserDao;
import com.hcltech.doctorpatient.dto.UserDTO;
import com.hcltech.doctorpatient.filter.JwtAuthRequestFilter;
import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
import com.hcltech.doctorpatient.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@ContextConfiguration(classes = {UserController.class, TestSecurityConfig.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AppointmentDao appointmentService;
    @MockitoBean
    private UserDao userDAO;
    @MockitoBean
    private JwtAuthRequestFilter jwtAuthRequestFilter;
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testCreateUser() throws Exception {
        UserDTO requestDto = new UserDTO();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setMobile("9876543210");
        requestDto.setPassword("securePass@123");
        requestDto.setRole("DOCTOR");

        UserDTO responseDto = new UserDTO();
        responseDto.setId(UUID.randomUUID());
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setMobile("9876543210");
        responseDto.setPassword("securePass@123");
        responseDto.setRole("DOCTOR");
        Mockito.when(userService.createUser(any())).thenReturn(responseDto);
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                /*.andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.mobile").value("9876543210"))
                .andExpect(jsonPath("$.role").value("USER"))*/;
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetAllUsers() throws Exception {
        UserDTO u1 = new UserDTO(UUID.randomUUID(), "Alice", "Smith", "9123456780", "pass1", "ADMIN");
        UserDTO u2 = new UserDTO(UUID.randomUUID(), "Bob", "Brown", "9988776655", "pass2", "USER");

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(u1, u2));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                        .value(2));
       /* mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
               *//* .andExpect(jsonPath("$.size()").value(2))*//*
                *//*.andExpect(jsonPath("$[0].first_name").value("Alice"))*//*;*/
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetUserById() throws Exception {
        UUID id = UUID.randomUUID();
        UserDTO responseDto = new UserDTO(id, "Charlie", "White", "9876543210", "pass3", "USER");

        Mockito.when(userService.getUserById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("Charlie"));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testUpdateUser() throws Exception {
        UUID id = UUID.randomUUID();

        UserDTO requestDto = new UserDTO();
        requestDto.setFirstName("Updated");
        requestDto.setLastName("User");
        requestDto.setMobile("9999999999");
        requestDto.setPassword("newPass");
        requestDto.setRole("ADMIN");

        UserDTO responseDto = new UserDTO(id, "Updated", "User", "9999999999", "newPass", "ADMIN");

        Mockito.when(userService.updateUser(any(UserDTO.class), eq(id))).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }





}