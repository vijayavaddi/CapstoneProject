package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.model.Appointment;
import com.hcltech.doctorpatient.repository.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentDaoTest {

    private AppointmentRepository appointmentRepository;
    private AppointmentDao appointmentDao;

    private Appointment sampleAppointment;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        appointmentDao = new AppointmentDao(appointmentRepository);

        sampleId = UUID.randomUUID();
        sampleAppointment = new Appointment();
        sampleAppointment.setId(sampleId);
        sampleAppointment.setStatus(Appointment.Status.SCHEDULED);
    }

    @Test
    void testCreateAppointment() {
        when(appointmentRepository.save(sampleAppointment)).thenReturn(sampleAppointment);
        Appointment result = appointmentDao.create(sampleAppointment);
        assertEquals(sampleAppointment, result);
        verify(appointmentRepository).save(sampleAppointment);
    }

    @Test
    void testUpdateAppointment() {
        when(appointmentRepository.save(sampleAppointment)).thenReturn(sampleAppointment);
        Appointment result = appointmentDao.update(sampleAppointment);
        assertEquals(sampleAppointment, result);
        verify(appointmentRepository).save(sampleAppointment);
    }

    @Test
    void testGetAllAppointments() {
        List<Appointment> appointments = Collections.singletonList(sampleAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        List<Appointment> result = appointmentDao.getall();
        assertEquals(1, result.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    void testGetByIdSuccess() {
        when(appointmentRepository.findById(sampleId)).thenReturn(Optional.of(sampleAppointment));
        Appointment result = appointmentDao.getById(sampleId);
        assertEquals(sampleAppointment, result);
        verify(appointmentRepository).findById(sampleId);
    }

    @Test
    void testGetByIdNotFound() {
        when(appointmentRepository.findById(sampleId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> appointmentDao.getById(sampleId));
        verify(appointmentRepository).findById(sampleId);
    }

    @Test
    void testCancelAppointment() {
        doNothing().when(appointmentRepository).deleteById(sampleId);
        appointmentDao.cancel(sampleId);
        verify(appointmentRepository).deleteById(sampleId);
    }

    @Test
    void testGetMyAppointments() {
        List<Appointment> appointments = Collections.singletonList(sampleAppointment);
        when(appointmentRepository.findByDoctorDoctorId(sampleId)).thenReturn(appointments);
        List<Appointment> result = appointmentDao.getMyAppointmets(sampleId);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByDoctorDoctorId(sampleId);
    }

    @Test
    void testMarkAsCompletedSuccess() {
        when(appointmentRepository.findById(sampleId)).thenReturn(Optional.of(sampleAppointment));
        when(appointmentRepository.save(any())).thenReturn(sampleAppointment);

        Appointment result = appointmentDao.markAsCompleted(sampleId);
        assertEquals(Appointment.Status.COMPLETED, result.getStatus());
        verify(appointmentRepository).findById(sampleId);
        verify(appointmentRepository).save(sampleAppointment);
    }

    @Test
    void testMarkAsCompletedNotFound() {
        when(appointmentRepository.findById(sampleId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> appointmentDao.markAsCompleted(sampleId));
        verify(appointmentRepository).findById(sampleId);
    }
}
