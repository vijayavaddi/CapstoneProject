package com.hcltech.doctorpatient.repository;

import com.hcltech.doctorpatient.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByDoctorDoctorId(UUID doctorId);
    //List<Appointment> findByPatient_PatientId(UUID id);
}
