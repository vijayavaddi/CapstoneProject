package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.dto.appointmentdto.AppointmentRequestDto;
import com.hcltech.doctorpatient.dto.appointmentdto.AppointmentResponseDto;
import com.hcltech.doctorpatient.dto.appointmentdto.DoctorAppointmentsDto;
import com.hcltech.doctorpatient.exception.DiseaseNotFoundException;
import com.hcltech.doctorpatient.exception.DoctorNotAvailableException;
import com.hcltech.doctorpatient.exception.DuplicateAppointmentException;
import com.hcltech.doctorpatient.model.*;
import com.hcltech.doctorpatient.repository.*;
import com.hcltech.doctorpatient.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock private AppointmentDao appointmentDao;
    @Mock private DiseaseRepository diseaseRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;

    private User user;
    private Patient patient;
    private Doctor doctor;
    private Disease disease;
    private Specialization specialization;
    private Appointment appointment;
    private AppointmentRequestDto requestDto;
    private UUID userId;
    private UUID doctorId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup RequestContextHolder for JWT
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(jwtUtil.extractUsernameFromToken("token")).thenReturn("9999999999");

        userId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMobile("9999999999");
        user.setRole(Role.PATIENT);

        patient = new Patient();
        patient.setPatientId(userId);
        patient.setUser(user);

        specialization = new Specialization();
        specialization.setId(UUID.randomUUID());
        specialization.setSpecializationName("Cardiology");

        disease = new Disease(UUID.randomUUID(), "Heart", specialization);

        doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setUser(user);
        doctor.setSpecialization(specialization);

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setFromTime(LocalDateTime.now().plusDays(1));
        appointment.setToTime(LocalDateTime.now().plusDays(1).plusHours(1));
        appointment.setStatus(Appointment.Status.SCHEDULED);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDisease(disease);

        requestDto = new AppointmentRequestDto();
        requestDto.setFromTime(appointment.getFromTime());
        requestDto.setToTime(appointment.getToTime());
        requestDto.setDescription("desc");
        requestDto.setDiseasename("Heart");
        requestDto.setMobile("9999999999");

        when(userRepository.findByMobile("9999999999")).thenReturn(Optional.of(user));
        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void create_success() {
        when(appointmentDao.getall()).thenReturn(Collections.emptyList());
        when(userRepository.findByMobile(requestDto.getMobile())).thenReturn(Optional.of(user));
        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(List.of(doctor));
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(Collections.emptyList());
        when(appointmentDao.create(any())).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentResponseDto dto = appointmentService.create(requestDto);

        assertNotNull(dto);
        assertEquals("Heart", dto.getDiseaseName());
        verify(appointmentDao).create(any());
    }

    @Test
    void create_duplicateAppointment_throwsException() {
        Appointment scheduled = new Appointment();
        scheduled.setPatient(patient);
        scheduled.setStatus(Appointment.Status.SCHEDULED);
        when(appointmentDao.getall()).thenReturn(List.of(scheduled));

        assertThrows(DuplicateAppointmentException.class, () -> appointmentService.create(requestDto));
        verify(appointmentDao, never()).create(any());
    }

    @Test
    void create_diseaseNotFound_throwsException() {
        when(appointmentDao.getall()).thenReturn(Collections.emptyList());
        when(userRepository.findByMobile(requestDto.getMobile())).thenReturn(Optional.of(user));
        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(diseaseRepository.findByName("Heart")).thenReturn(null);

        assertThrows(DiseaseNotFoundException.class, () -> appointmentService.create(requestDto));
    }

    @Test
    void create_noDoctors_throwsException() {
        when(appointmentDao.getall()).thenReturn(Collections.emptyList());
        when(userRepository.findByMobile(requestDto.getMobile())).thenReturn(Optional.of(user));
        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(Collections.emptyList());

        assertThrows(DoctorNotAvailableException.class, () -> appointmentService.create(requestDto));
    }

    @Test
    void create_doctorNotAvailable_throwsException() {
        when(appointmentDao.getall()).thenReturn(Collections.emptyList());
        when(userRepository.findByMobile(requestDto.getMobile())).thenReturn(Optional.of(user));
        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(List.of(doctor));
        List<Appointment> fullDay = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Appointment a = new Appointment();
            a.setStatus(Appointment.Status.SCHEDULED);
            a.setFromTime(requestDto.getFromTime());
            a.setToTime(requestDto.getToTime());
            fullDay.add(a);
        }
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(fullDay);

        assertThrows(DoctorNotAvailableException.class, () -> appointmentService.create(requestDto));
    }

    @Test
    void update_success() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(List.of(doctor));
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(Collections.emptyList());
        when(appointmentDao.update(any())).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentResponseDto dto = appointmentService.update(appointmentId, requestDto);

        assertNotNull(dto);
        assertEquals("Heart", dto.getDiseaseName());
        verify(appointmentDao).update(any());
    }

    @Test
    void update_appointmentNotFound_throwsException() {
        when(appointmentDao.getById(appointmentId)).thenThrow(new EntityNotFoundException("not found"));

        assertThrows(EntityNotFoundException.class, () -> appointmentService.update(appointmentId, requestDto));
    }

    @Test
    void getall_success() {
        when(appointmentDao.getall()).thenReturn(List.of(appointment));
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        List<AppointmentResponseDto> dtos = appointmentService.getall();

        assertEquals(1, dtos.size());
        assertEquals("Heart", dtos.get(0).getDiseaseName());
    }

    @Test
    void getonebyId_success() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentResponseDto dto = appointmentService.getonebyId(appointmentId);

        assertNotNull(dto);
        assertEquals("Heart", dto.getDiseaseName());
    }

    @Test
    void delete_success() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(appointmentDao.create(any())).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentResponseDto dto = appointmentService.delete(appointmentId);

        assertEquals("CANCELLED", dto.getStatus());
        verify(appointmentDao).create(appointment);
    }

    @Test
    void getMyAppointments_success() {
        when(userRepository.findByMobile("9999999999")).thenReturn(Optional.of(user));
        when(appointmentDao.getMyAppointmets(userId)).thenReturn(List.of(appointment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<DoctorAppointmentsDto> dtos = appointmentService.getMyAppointments();

        assertEquals(1, dtos.size());
        assertEquals("Heart", dtos.get(0).getDisease_name());
    }

    @Test
    void getMyAppointments_userNotFound_throwsException() {
        when(userRepository.findByMobile("9999999999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.getMyAppointments());
    }

    @Test
    void markAsCompleted_success() {
        when(appointmentDao.markAsCompleted(appointmentId)).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentResponseDto dto = appointmentService.markAsCompleted(appointmentId);

        assertNotNull(dto);
        assertEquals("Heart", dto.getDiseaseName());
    }

    @Test
    void toDto_appointment_success() {
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));
        when(userRepository.findByMobile("9999999999")).thenReturn(Optional.of(user));

        AppointmentResponseDto dto = appointmentService.toDto(appointment);

        assertEquals("Heart", dto.getDiseaseName());
        assertEquals("John Doe", dto.getPatientName());
    }

    @Test
    void toDoctorDto_appointment_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        DoctorAppointmentsDto dto = appointmentService.toDoctorDto(appointment);

        assertEquals("Heart", dto.getDisease_name());
        assertEquals("John Doe", dto.getPatient_name());
    }

    @Test
    void getUsernameFromJWT_success() {
        String username = appointmentService.getUsernameFromJWT();
        assertEquals("9999999999", username);
    }

    @Test
    void getUsernameFromJWT_noHeader_throwsException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThrows(JwtException.class, () -> appointmentService.getUsernameFromJWT());
    }

    @Test
    void getUsernameFromJWT_invalidHeader_throwsException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Invalid");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThrows(JwtException.class, () -> appointmentService.getUsernameFromJWT());
    }
    // Test for hasTimeChanged (private, but covered via update)
    @Test
    void update_timeChanged_triggersReassignDoctor() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(List.of(doctor));
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(Collections.emptyList());
        when(appointmentDao.update(any())).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setFromTime(appointment.getFromTime().plusHours(2)); // triggers hasTimeChanged
        dto.setDiseasename("Heart");
        dto.setMobile("9999999999");

        AppointmentResponseDto response = appointmentService.update(appointmentId, dto);

        assertNotNull(response);
        verify(appointmentDao).update(any());
    }

    // Test for updateDiseaseIfNeeded: disease not found
    @Test
    void update_diseaseNotFound_throwsDoctorNotAvailableException() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(diseaseRepository.findByName("Unknown")).thenReturn(null);

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setDiseasename("Unknown");

        assertThrows(DoctorNotAvailableException.class, () -> appointmentService.update(appointmentId, dto));
    }

    // Test for reassignDoctor: no doctors
    @Test
    void reassignDoctor_noDoctors_throwsException() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(Collections.emptyList());

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setDiseasename("Heart");

        assertThrows(DoctorNotAvailableException.class, () -> appointmentService.update(appointmentId, dto));
    }

    // Test for reassignDoctor: no available doctor
    @Test
    void reassignDoctor_noAvailableDoctor_throwsException() {
        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(List.of(doctor));
        List<Appointment> fullDay = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Appointment a = new Appointment();
            a.setStatus(Appointment.Status.SCHEDULED);
            a.setFromTime(appointment.getFromTime());
            a.setToTime(appointment.getToTime());
            fullDay.add(a);
        }
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(fullDay);

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setDiseasename("Heart");

        assertThrows(DoctorNotAvailableException.class, () -> appointmentService.update(appointmentId, dto));
    }

    // Test for findAvailableDoctor: doctor available
    @Test
    void findAvailableDoctor_doctorAvailable_returnsDoctor() {
        List<Doctor> doctors = List.of(doctor);
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(Collections.emptyList());

        Doctor result = invokeFindAvailableDoctor(doctors, appointment);
        assertNotNull(result);
    }

    // Helper to invoke private method via reflection
    private Doctor invokeFindAvailableDoctor(List<Doctor> doctors, Appointment appointment) {
        try {
            var method = AppointmentService.class.getDeclaredMethod("findAvailableDoctor", List.class, Appointment.class);
            method.setAccessible(true);
            return (Doctor) method.invoke(appointmentService, doctors, appointment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Test for toUpdateEntity: patient update
    @Test
    void update_patientChanged_updatesPatient() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setMobile("8888888888");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setRole(Role.PATIENT);

        Patient newPatient = new Patient();
        newPatient.setPatientId(newUser.getId());
        newPatient.setUser(newUser);

        when(appointmentDao.getById(appointmentId)).thenReturn(appointment);
        when(userRepository.findByMobile("8888888888")).thenReturn(Optional.of(newUser));
        when(patientRepository.findById(newUser.getId())).thenReturn(Optional.of(newPatient));
        when(diseaseRepository.findByName("Heart")).thenReturn(disease);
        when(doctorRepository.findBySpecializationId(specialization.getId())).thenReturn(List.of(doctor));
        when(appointmentDao.getMyAppointmets(doctorId)).thenReturn(Collections.emptyList());
        when(appointmentDao.update(any())).thenReturn(appointment);
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(user));

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setMobile("8888888888");
        dto.setDiseasename("Heart");

        AppointmentResponseDto response = appointmentService.update(appointmentId, dto);

        assertNotNull(response);
        verify(appointmentDao).update(any());
    }
}