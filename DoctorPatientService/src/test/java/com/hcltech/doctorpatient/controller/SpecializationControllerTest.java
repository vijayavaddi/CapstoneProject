package com.hcltech.doctorpatient.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.doctorpatient.config.TestSecurityConfig;
import com.hcltech.doctorpatient.dto.SpecializationDto;
import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.service.SpecializationService;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecializationController.class)
@Import(TestSecurityConfig.class)

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {SpecializationController.class, TestSecurityConfig.class})
class SpecializationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SpecializationService specializationService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AppointmentDao appointmentService;
    private SpecializationDto specializationDto;

    @BeforeEach
    void setUp() {
        specializationDto = new SpecializationDto(UUID.randomUUID(), "Cardiology");
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testCreateSpecialization() throws Exception {
        SpecializationDto requestDto = new SpecializationDto();
        requestDto.setSpecialization("Cardiology");

        SpecializationDto responseDto = new SpecializationDto(UUID.randomUUID(), "Cardiology");

        Mockito.when(specializationService.create(any(SpecializationDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/doctorPatientService/v1/specializations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialization").value("Cardiology"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllSpecializations() throws Exception {
        Mockito.when(specializationService.getAll()).thenReturn(Arrays.asList(specializationDto));

        mockMvc.perform(get("/api/doctorPatientService/v1/specializations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialization").value("Cardiology"));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetSpecializationById() throws Exception {
        UUID id = UUID.randomUUID();
        SpecializationDto responseDto = new SpecializationDto(id, "Orthopedics");

        Mockito.when(specializationService.getOneById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/doctorPatientService/v1/specializations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.specialization").value("Orthopedics"));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testUpdateSpecialization() throws Exception {
        UUID id = UUID.randomUUID();
        SpecializationDto requestDto = new SpecializationDto(id, "Updated Specialization");

        Mockito.when(specializationService.update(any(SpecializationDto.class))).thenReturn(requestDto);

        mockMvc.perform(put("/api/doctorPatientService/v1/specializations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.specialization").value("Updated Specialization"));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testDeleteSpecialization() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(specializationService).delete(id);

        mockMvc.perform(delete("/api/doctorPatientService/v1/specializations/{id}", id))
                .andExpect(status().isOk());
    }

}