package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.dao.service.PatientDAO;
import com.hcltech.doctorpatient.dto.patientdto.PatientRequestDto;
import com.hcltech.doctorpatient.dto.patientdto.PatientResponseDto;
import com.hcltech.doctorpatient.exception.PatientExceptionHandler;
import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
import com.hcltech.doctorpatient.model.Patient;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.AppointmentRepository;
import com.hcltech.doctorpatient.repository.PatientRepository;
import com.hcltech.doctorpatient.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientDAO {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public Patient create(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    public Patient getOneById(UUID id) {
        return patientRepository.findById(id).orElseThrow(() -> new PatientExceptionHandler("Patient not found with id: " + id));
    }

    public void delete(UUID id) {
        patientRepository.deleteById(id);
    }

    public Patient update(Patient patient) {
        return patientRepository.save(patient);
    }

}