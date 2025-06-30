package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.DoctorDao;
import com.hcltech.doctorpatient.dto.doctordto.DoctorRequestDto;
import com.hcltech.doctorpatient.dto.doctordto.DoctorResponseDto;
import com.hcltech.doctorpatient.model.Doctor;
import com.hcltech.doctorpatient.model.Specialization;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.SpecializationRepository;
import com.hcltech.doctorpatient.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    private DoctorDao doctorDao;
    private ModelMapper modelMapper;
    private SpecializationRepository specializationRepository;
    private UserRepository userRepository;
    private AppointmentService appointmentService;
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        doctorDao = mock(DoctorDao.class);
        modelMapper = new ModelMapper();
        specializationRepository = mock(SpecializationRepository.class);
        userRepository = mock(UserRepository.class);
        appointmentService = mock(AppointmentService.class);
        doctorService = new DoctorService(doctorDao, modelMapper, specializationRepository, userRepository, appointmentService);
    }

    @Test
    void testGetAll_Success() {
        Doctor doctor = new Doctor();
        doctor.setUser(new User());
        doctor.setSpecialization(new Specialization());
        when(doctorDao.getAll()).thenReturn(List.of(doctor));
        List<DoctorResponseDto> result = doctorService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAll_EmptyListThrows() {
        when(doctorDao.getAll()).thenReturn(Collections.emptyList());
        assertThrows(EntityNotFoundException.class, () -> doctorService.getAll());
    }

    @Test
    void testCreate_Success() {
        UUID userId = UUID.randomUUID();
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setSpecialist("Cardiology");
        dto.setExperience("5 years");
        dto.setQualification("MD");

        Specialization specialization = new Specialization();
        specialization.setSpecializationName("Cardiology");
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMobile("1234567890");
        Doctor doctor = new Doctor();
        doctor.setDoctorId(UUID.randomUUID());

        when(appointmentService.getUsernameFromJWT()).thenReturn("1234567890");
        when(userRepository.findByMobile("1234567890")).thenReturn(Optional.of(user));
        when(specializationRepository.getBySpecializationName("Cardiology")).thenReturn(specialization);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setDoctorId(UUID.randomUUID());
            return null;
        }).when(doctorDao).create(any(Doctor.class));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UUID doctorId = doctorService.create(dto, userId);
        assertNotNull(doctorId);
    }

    @Test
    void testCreate_SpecializationNotFound() {
        UUID userId = UUID.randomUUID();
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setSpecialist("Unknown");
        when(specializationRepository.getBySpecializationName("Unknown")).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> doctorService.create(dto, userId));
    }

    @Test
    void testCreate_UserNotFound() {
        UUID userId = UUID.randomUUID();
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setSpecialist("Cardiology");
        Specialization specialization = new Specialization();
        when(specializationRepository.getBySpecializationName("Cardiology")).thenReturn(specialization);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> doctorService.create(dto, userId));
    }

    @Test
    void testGetOneById_Success() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setUser(new User());
        doctor.setSpecialization(new Specialization());
        when(doctorDao.getOneById(doctorId)).thenReturn(doctor);
        DoctorResponseDto dto = doctorService.getOneById(doctorId);
        assertNotNull(dto);
    }

    @Test
    void testGetOneById_NotFound() {
        UUID doctorId = UUID.randomUUID();
        when(doctorDao.getOneById(doctorId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> doctorService.getOneById(doctorId));
    }

    @Test
    void testDelete_Success() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        when(doctorDao.getOneById(doctorId)).thenReturn(doctor);
        doNothing().when(doctorDao).deleteOneById(doctorId);
        assertDoesNotThrow(() -> doctorService.delete(doctorId));
        verify(doctorDao).deleteOneById(doctorId);
    }

    @Test
    void testDelete_NotFound() {
        UUID doctorId = UUID.randomUUID();
        when(doctorDao.getOneById(doctorId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> doctorService.delete(doctorId));
    }

    @Test
    void testUpdate_Success() {
        UUID userId = UUID.randomUUID();
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setSpecialist("Cardiology");
        dto.setExperience("10 years");
        dto.setQualification("PhD");

        Doctor doctor = new Doctor();
        doctor.setUser(new User());
        doctor.setSpecialization(new Specialization());

        Specialization specialization = new Specialization();
        specialization.setSpecializationName("Cardiology");

        when(doctorDao.getOneById(userId)).thenReturn(doctor);
        when(specializationRepository.getBySpecializationName("Cardiology")).thenReturn(specialization);
        when(doctorDao.update(doctor)).thenReturn(doctor);

        DoctorResponseDto response = doctorService.update(dto, userId);
        assertNotNull(response);
        assertEquals("Cardiology", response.getSpecialist());
    }
}