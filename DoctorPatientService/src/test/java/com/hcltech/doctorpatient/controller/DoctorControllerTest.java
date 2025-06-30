package com.hcltech.doctorpatient.controller;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hcltech.doctorpatient.config.TestSecurityConfig;
import com.hcltech.doctorpatient.dto.doctordto.DoctorRequestDto;
import com.hcltech.doctorpatient.dto.doctordto.DoctorResponseDto;
import com.hcltech.doctorpatient.filter.JwtAuthRequestFilter;
import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.service.DoctorService;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
import com.hcltech.doctorpatient.util.JwtUtil;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {DoctorController.class, TestSecurityConfig.class})
@WebMvcTest(controllers = DoctorController.class)
@Import(TestSecurityConfig.class)
class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DoctorService doctorService;
    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;
    @MockitoBean
    private AppointmentDao appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthRequestFilter jwtAuthRequestFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testCreateDoctor() throws Exception {
        DoctorRequestDto doctorRequestDto1 = new DoctorRequestDto();
        doctorRequestDto1.setExperience("1");
        doctorRequestDto1.setQualification("MBBS");
        doctorRequestDto1.setSpecialist("Cardiology");

        DoctorResponseDto doctorResponseDto1 = new DoctorResponseDto();
        UUID doctorId = UUID.randomUUID();
        doctorResponseDto1.setDoctorId(doctorId);
        doctorResponseDto1.setExperience("1");
        doctorResponseDto1.setSpecialist("Cardiology");
        doctorResponseDto1.setMobileNumber("1111111111");

        Mockito.when(doctorService.create(any(), eq(doctorId))).thenReturn(doctorId);

        mockMvc.perform(post("/api/doctors")
                        .param("userId", doctorId.toString())

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequestDto1)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetAllDoctors() throws Exception {
        DoctorResponseDto d1 = new DoctorResponseDto();
        d1.setDoctorId(UUID.randomUUID());
        d1.setName("Dr. Alice");
        d1.setMobileNumber("9876543210");
        d1.setExperience("8 years");
        d1.setSpecialist("Dermatologist");

        DoctorResponseDto d2 = new DoctorResponseDto();
        d2.setDoctorId(UUID.randomUUID());
        d2.setName("Dr. Bob");
        d2.setMobileNumber("9123456780");
        d2.setExperience("12 years");
        d2.setSpecialist("Neurologist");

        Mockito.when(doctorService.getAll()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Dr. Alice"))
                .andExpect(jsonPath("$[1].name").value("Dr. Bob"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetDoctorById() throws Exception {
        UUID doctorId = UUID.randomUUID();

        DoctorResponseDto responseDto = new DoctorResponseDto();
        responseDto.setDoctorId(doctorId);
        responseDto.setName("Dr. Clara");
        responseDto.setMobileNumber("9988776655");
        responseDto.setExperience("15 years");
        responseDto.setSpecialist("Oncologist");

        Mockito.when(doctorService.getOneById(doctorId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/doctors/{id}", doctorId)
                        .param("doctorId", doctorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.name").value("Dr. Clara"));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testUpdateDoctor() throws Exception {
        UUID doctorId = UUID.randomUUID();

        DoctorRequestDto requestDto = new DoctorRequestDto();
        requestDto.setExperience("20 years");
        requestDto.setQualification("MBBS, MS");
        requestDto.setSpecialist("Orthopedic");

        DoctorResponseDto responseDto = new DoctorResponseDto();
        responseDto.setDoctorId(doctorId);
        responseDto.setName("Dr. Updated");
        responseDto.setMobileNumber("9999999999");
        responseDto.setExperience("20 years");
        responseDto.setSpecialist("Orthopedic");

        Mockito.when(doctorService.update(any(DoctorRequestDto.class), eq(doctorId))).thenReturn(responseDto);

        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .param("doctorId", doctorId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.experience").value("20 years"))
                .andExpect(jsonPath("$.specialist").value("Orthopedic"));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testDeleteDoctor() throws Exception {
        UUID doctorId = UUID.randomUUID();

        Mockito.doNothing().when(doctorService).delete(doctorId);

        mockMvc.perform(delete("/api/doctors/{id}", doctorId)
                        .param("doctorId", doctorId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor deleted successfully: " + doctorId));
    }

}
