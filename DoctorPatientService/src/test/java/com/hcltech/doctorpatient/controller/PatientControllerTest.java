package com.hcltech.doctorpatient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.doctorpatient.config.TestSecurityConfig;
import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.dto.patientdto.PatientRequestDto;
import com.hcltech.doctorpatient.dto.patientdto.PatientResponseDto;
import com.hcltech.doctorpatient.filter.JwtAuthRequestFilter;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
import com.hcltech.doctorpatient.service.PatientService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {PatientController.class, TestSecurityConfig.class})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentDao appointmentService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthRequestFilter jwtAuthRequestFilter;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreatePatient() throws Exception {
        PatientRequestDto requestDTO = new PatientRequestDto();
        requestDTO.setAge((short) 20);
        requestDTO.setBloodGroup("O+");
        requestDTO.setGender("Female");
        PatientResponseDto responseDTO = new PatientResponseDto();
        responseDTO.setAge((short) 28);
        responseDTO.setBloodGroup("O+");
        responseDTO.setGender("Female");
        UUID id = UUID.randomUUID();
        when(patientService.create(requestDTO, id)).thenReturn(responseDTO);
        mockMvc.perform(post("/api/patients")
                        .param("id", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isCreated());
         /* .andExpect(jsonPath("$.age").value(20))
        .andExpect(jsonPath("$.bloodGroup").value("O+"))
        .andExpect(jsonPath("$.gender").value("Female"))*/
    }

    @Test
    public void testUpdatePatient() throws Exception{
        // Arrange
        UUID patientId = UUID.randomUUID();
        PatientRequestDto requestDto = new PatientRequestDto();
        requestDto.setAge((short) 20);
        requestDto.setBloodGroup("O+");
        requestDto.setGender("Female");

        PatientResponseDto responseDto = new PatientResponseDto();
        responseDto.setAge((short) 28);
        responseDto.setBloodGroup("O+");
        responseDto.setGender("Female");

        Mockito.when(patientService.update(any(PatientRequestDto.class), eq(patientId)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetAllPatients() throws Exception {
        PatientResponseDto p1 = new PatientResponseDto();
        p1.setId(UUID.randomUUID());
        p1.setName("Alice");
        p1.setMobileNumber("9876543210");
        p1.setAge((short) 25);
        p1.setBloodGroup("A+");
        p1.setGender("Female");
        ;

        PatientResponseDto p2 = new PatientResponseDto();
        p2.setId(UUID.randomUUID());
        p2.setName("Bob");
        p2.setMobileNumber("9123456780");
        p2.setAge((short) 40);
        p2.setBloodGroup("B-");
        p2.setGender("Male");

        when(patientService.getAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetPatientById() throws Exception {
        UUID id = UUID.randomUUID();

        PatientResponseDto responseDto = new PatientResponseDto();
        responseDto.setAge((short) 28);
        responseDto.setBloodGroup("AB+");
        responseDto.setGender("Male");

        when(patientService.getOneById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(28))
                .andExpect(jsonPath("$.bloodGroup").value("AB+"))
                .andExpect(jsonPath("$.gender").value("Male"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testDeletePatient() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(patientService).delete(id);

        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNoContent());
    }


}