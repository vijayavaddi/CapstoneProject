package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.exception.PatientExceptionHandler;
import com.hcltech.doctorpatient.model.Patient;
import com.hcltech.doctorpatient.repository.AppointmentRepository;
import com.hcltech.doctorpatient.repository.PatientRepository;
import com.hcltech.doctorpatient.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientDaoTest {

    private PatientRepository patientRepository;
    private UserRepository userRepository;
    private AppointmentRepository appointmentRepository;
    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        userRepository = mock(UserRepository.class);
        appointmentRepository = mock(AppointmentRepository.class);
        patientDAO = new PatientDAO(patientRepository, userRepository, appointmentRepository);
    }

    @Test
    void testCreate() {
        Patient patient = new Patient();
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientDAO.create(patient);

        assertEquals(patient, result);
        verify(patientRepository).save(patient);
    }

    @Test
    void testGetAll() {
        List<Patient> patients = Arrays.asList(new Patient(), new Patient());
        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientDAO.getAll();

        assertEquals(2, result.size());
        verify(patientRepository).findAll();
    }

    @Test
    void testGetOneById_Found() {
        UUID id = UUID.randomUUID();
        Patient patient = new Patient();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        Patient result = patientDAO.getOneById(id);

        assertEquals(patient, result);
        verify(patientRepository).findById(id);
    }

    @Test
    void testGetOneById_NotFound() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PatientExceptionHandler.class, () -> patientDAO.getOneById(id));
        verify(patientRepository).findById(id);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        doNothing().when(patientRepository).deleteById(id);

        patientDAO.delete(id);

        verify(patientRepository).deleteById(id);
    }

    @Test
    void testUpdate() {
        Patient patient = new Patient();
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientDAO.update(patient);

        assertEquals(patient, result);
        verify(patientRepository).save(patient);
    }
}