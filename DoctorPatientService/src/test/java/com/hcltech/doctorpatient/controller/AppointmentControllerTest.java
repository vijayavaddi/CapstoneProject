package com.hcltech.doctorpatient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.doctorpatient.config.TestSecurityConfig;
import com.hcltech.doctorpatient.dto.appointmentdto.AppointmentRequestDto;
import com.hcltech.doctorpatient.dto.appointmentdto.AppointmentResponseDto;
import com.hcltech.doctorpatient.dto.appointmentdto.DoctorAppointmentsDto;
import com.hcltech.doctorpatient.filter.JwtAuthRequestFilter;
import com.hcltech.doctorpatient.service.AppointmentService;
import com.hcltech.doctorpatient.service.JpaUserDetailsService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AppointmentController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {AppointmentController.class, TestSecurityConfig.class})
public class AppointmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AppointmentService appointmentService;
    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;
    @MockitoBean
    private JwtAuthRequestFilter jwtAuthRequestFilter;
    @Autowired
    private ObjectMapper objectMapper;


    AppointmentResponseDto responseDto = new AppointmentResponseDto();
    AppointmentRequestDto requestDto = new AppointmentRequestDto();

    @BeforeEach
    void setUp(){
        LocalDateTime time = LocalDateTime.now();
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDiseasename("Fever");
        requestDto.setMobile("9654325678");
        requestDto.setDescription("High temperature and fatigue");
        requestDto.setToTime(time);
        requestDto.setFromTime(time.minusHours(1));

        AppointmentResponseDto responseDto = new AppointmentResponseDto();
        responseDto.setFromTime(time.minusHours(1));
        responseDto.setToTime(time);
        responseDto.setDoctorName("Doctor");
        responseDto.setPatientName("Patient");
        responseDto.setDiseaseName("Fever");
        responseDto.setStatus("SCHEDULED");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetAllAppointments() throws Exception {
        AppointmentResponseDto a1 = new AppointmentResponseDto();
        a1.setStatus("BOOKED");
        AppointmentResponseDto a2 = new AppointmentResponseDto();
        a2.setStatus("CANCELLED");
        Mockito.when(appointmentService.getall()).thenReturn(Arrays.asList(a1,a2));
        mockMvc.perform(get("/api/appointment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testGetAppointmentById() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        UUID id = UUID.randomUUID();

        Mockito.when(appointmentService.getonebyId(id)).thenReturn(responseDto);
        mockMvc.perform(get("/api/appointment/getbyid",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void testCancelAppointment() throws Exception {
        UUID id = UUID.randomUUID();
        AppointmentResponseDto responseDto = new AppointmentResponseDto();
//        responseDto.setId(id);
        responseDto.setStatus("CANCELLED");
        Mockito.when(appointmentService.delete(id)).thenReturn(responseDto);
        mockMvc.perform(post("/api/appointment/cancel")
                        .param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

    }
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    @Test
    void updateAppointment() throws Exception {
        UUID id = UUID.randomUUID();
        AppointmentRequestDto requestDto = new AppointmentRequestDto();
        requestDto.setDiseasename("Fever");
        requestDto.setMobile("9654325678");
        requestDto.setDescription("High temperature and fatigue");
        requestDto.setToTime(LocalDateTime.now());
        requestDto.setFromTime(LocalDateTime.now().minusHours(1));

        AppointmentResponseDto responseDto = new AppointmentResponseDto();
        responseDto.setStatus("RESCHEDULED");

        Mockito.when(appointmentService.update(eq(id), any(AppointmentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/appointment/reschedule/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESCHEDULED"));
    }


    @Test
    @WithMockUser(username = "doctoruser", roles = {"DOCTOR"})
    void testMarkAsCompleted() throws Exception {
        UUID id = UUID.randomUUID();
        AppointmentResponseDto responseDto = new AppointmentResponseDto();
        responseDto.setStatus("COMPLETED");

        Mockito.when(appointmentService.markAsCompleted(id)).thenReturn(responseDto);

        mockMvc.perform(put("/api/appointment/{id}/complete", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
    @Test
    @WithMockUser(username = "doctoruser", roles = {"DOCTOR"})
    void testGetMyAppointments() throws Exception {
        DoctorAppointmentsDto dto1 = new DoctorAppointmentsDto();
        DoctorAppointmentsDto dto2 = new DoctorAppointmentsDto();
        Mockito.when(appointmentService.getMyAppointments()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/appointment/my-appointments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}