package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.model.Appointment;
import com.hcltech.doctorpatient.repository.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppointmentDao {
    private final AppointmentRepository appointmentRepository;

    public AppointmentDao(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment create(Appointment appointment){
        return appointmentRepository.save(appointment);
    }
    public Appointment update(Appointment appointment){
        return appointmentRepository.save(appointment);
    }
    public List<Appointment> getall(){
        return appointmentRepository.findAll();
    }
    public Appointment getById(UUID id){
        return appointmentRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Appointement not found"));
    }
    public void cancel(UUID id){
        appointmentRepository.deleteById(id);
    }
    public List<Appointment> getMyAppointmets(UUID id){
        return appointmentRepository.findByDoctorDoctorId(id);
    }

    public Appointment markAsCompleted(UUID id) {
        Appointment appointment=appointmentRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Appointment not found"));
        appointment.setStatus(Appointment.Status.COMPLETED);
        return appointmentRepository.save(appointment);
    }
}
