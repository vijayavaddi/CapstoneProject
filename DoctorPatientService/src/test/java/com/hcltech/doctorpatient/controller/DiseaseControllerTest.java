package com.hcltech.doctorpatient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.doctorpatient.config.TestSecurityConfig;
import com.hcltech.doctorpatient.dto.DiseaseDTO;
import com.hcltech.doctorpatient.filter.JwtAuthRequestFilter;
import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.service.DiseaseService;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = DiseaseController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {DiseaseController.class, TestSecurityConfig.class})
public class DiseaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiseaseService diseaseService;

    @MockitoBean
    private AppointmentDao appointmentService;

    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthRequestFilter jwtAuthRequestFilter;

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testCreateDisease() throws Exception {
        DiseaseDTO dto = new DiseaseDTO();
        dto.setDiseaseId(UUID.randomUUID());
        dto.setDiseaseName("Flu");
        dto.setSpecializatonName("Cardiology");

        Mockito.when(diseaseService.createDisease(any(DiseaseDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/diseases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetAllDiseases() throws Exception {
        DiseaseDTO dto1 = new DiseaseDTO();
        dto1.setDiseaseId(UUID.randomUUID());
        dto1.setDiseaseName("Flu");
        dto1.setSpecializatonName("Cardiology");

        DiseaseDTO dto2 = new DiseaseDTO();
        dto2.setDiseaseId(UUID.randomUUID());
        dto2.setDiseaseName("Cold");
        dto2.setSpecializatonName("General");

        List<DiseaseDTO> diseaseList = List.of(dto1, dto2);

        Mockito.when(diseaseService.getAllDiseases()).thenReturn(diseaseList);

        mockMvc.perform(get("/api/diseases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetDiseaseById() throws Exception {
        UUID id = UUID.randomUUID();
        DiseaseDTO dto = new DiseaseDTO();
        dto.setDiseaseId(id);
        dto.setDiseaseName("Flu");
        dto.setSpecializatonName("Cardiology");

        Mockito.when(diseaseService.getDiseaseById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/diseases/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diseaseId").value(id.toString()))
                .andExpect(jsonPath("$.diseaseName").value("Flu"))
                .andExpect(jsonPath("$.specializatonName").value("Cardiology"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testDeleteDisease() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(diseaseService).deleteDisease(id);
        mockMvc.perform(delete("/api/diseases/{id}", id))
                .andExpect(status().isNoContent());
    }



}
