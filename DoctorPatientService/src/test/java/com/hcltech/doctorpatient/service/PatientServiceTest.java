package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.PatientDAO;
import com.hcltech.doctorpatient.dto.patientdto.PatientRequestDto;
import com.hcltech.doctorpatient.dto.patientdto.PatientResponseDto;
import com.hcltech.doctorpatient.model.Patient;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private AppointmentService appointmentService;
    private PatientDAO patientDAO;
    private UserRepository userRepository;
    private PatientService patientService;

    @BeforeEach
    void setUp() throws Exception {
        appointmentService = mock(AppointmentService.class);
        patientDAO = mock(PatientDAO.class);
        userRepository = mock(UserRepository.class);

        patientService = new PatientService(appointmentService, patientDAO,userRepository);

        // Inject userRepository via reflection
        var field = PatientService.class.getDeclaredField("userRepository");
        field.setAccessible(true);
        field.set(patientService, userRepository);
    }

    @Test
    void testCreate_Success() {
        UUID userId = UUID.randomUUID();
        PatientRequestDto request = new PatientRequestDto();
        request.setAge((short) 30);
        request.setGender("MALE");
        request.setBloodGroup("A+");
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMobile("1234567890");
        Patient patient = new Patient();
        patient.setAge((short) 30);
        patient.setGender(Patient.Gender.MALE);
        patient.setBloodGroup("A+");
        patient.setUser(user);
        when(appointmentService.getUsernameFromJWT()).thenReturn("1234567890");
        when(userRepository.findByMobile("1234567890")).thenReturn(Optional.of(user));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(patientDAO.create(any(Patient.class))).thenReturn(patient);
        PatientResponseDto result = patientService.create(request, userId);
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("1234567890", result.getMobileNumber());
        assertEquals("MALE", result.getGender());
        assertEquals("A+", result.getBloodGroup());
        verify(patientDAO).create(any(Patient.class));
    }

    @Test
    void testCreate_UserNotFound() {
        UUID userId = UUID.randomUUID();
        PatientRequestDto request = new PatientRequestDto();
        request.setAge((short) 25);
        request.setGender("FEMALE");
        request.setBloodGroup("B+");

        User user = new User();
        user.setMobile("9876543210");

        when(appointmentService.getUsernameFromJWT()).thenReturn("9876543210");
        when(userRepository.findByMobile("9876543210")).thenReturn(Optional.of(user));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> patientService.create(request, userId));
        assertTrue(ex.getMessage().contains("User Not found with Id"));
    }

    @Test
    void testGetAll() {
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setMobile("1112223333");

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setGender(Patient.Gender.FEMALE);
        patient.setBloodGroup("O+");

        when(patientDAO.getAll()).thenReturn(List.of(patient));

        List<PatientResponseDto> result = patientService.getAll();

        assertEquals(1, result.size());
        assertEquals("Alice Smith", result.get(0).getName());
        assertEquals("O+", result.get(0).getBloodGroup());
        verify(patientDAO).getAll();
    }

    @Test
    void testGetOneById() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setFirstName("Bob");
        user.setLastName("Brown");
        user.setMobile("5556667777");

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setGender(Patient.Gender.MALE);
        patient.setBloodGroup("AB-");

        when(patientDAO.getOneById(id)).thenReturn(patient);

        PatientResponseDto result = patientService.getOneById(id);

        assertEquals("Bob Brown", result.getName());
        assertEquals("AB-", result.getBloodGroup());
        verify(patientDAO).getOneById(id);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        doNothing().when(patientDAO).delete(id);

        patientService.delete(id);

        verify(patientDAO).delete(id);
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        PatientRequestDto request = new PatientRequestDto();
        request.setAge((short) 40);
        request.setGender("MALE");
        request.setBloodGroup("B-");

        User user = new User();
        user.setFirstName("Carl");
        user.setLastName("White");
        user.setMobile("8889990000");

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setGender(Patient.Gender.FEMALE);
        patient.setBloodGroup("A+");
        patient.setAge((short) 35);

        when(patientDAO.getOneById(id)).thenReturn(patient);


        PatientResponseDto result = patientService.update(request, id);
        when(patientDAO.update(any(Patient.class))).thenReturn(patient);
        assertEquals("Carl White", result.getName());
        assertEquals("MALE", result.getGender());
        assertEquals("B-", result.getBloodGroup());
        assertEquals("8889990000", result.getMobileNumber());
        verify(patientDAO).update(patient);
    }

    @Test
    void testToDtoList() {
        User user = new User();
        user.setFirstName("Eve");
        user.setLastName("Black");
        user.setMobile("2223334444");

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setGender(Patient.Gender.FEMALE);
        patient.setBloodGroup("O-");

        List<PatientResponseDto> dtos = patientService.toDto(List.of(patient));
        assertEquals(1, dtos.size());
        assertEquals("Eve Black", dtos.get(0).getName());
    }
}