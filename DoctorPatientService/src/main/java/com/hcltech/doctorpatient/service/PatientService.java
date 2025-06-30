package com.hcltech.doctorpatient.service;


import com.hcltech.doctorpatient.dao.service.PatientDAO;
import com.hcltech.doctorpatient.dto.patientdto.PatientRequestDto;
import com.hcltech.doctorpatient.dto.patientdto.PatientResponseDto;
import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
import com.hcltech.doctorpatient.model.Patient;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.AppointmentRepository;
import com.hcltech.doctorpatient.repository.PatientRepository;
import com.hcltech.doctorpatient.repository.UserRepository;
import com.hcltech.doctorpatient.service.AppointmentService;
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
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final AppointmentService appointmentService;
    private final PatientDAO patientDAO;
    private final UserRepository userRepository;
    public PatientResponseDto create(PatientRequestDto patientRequestDto, UUID id) {
        Patient patient=toEntity(patientRequestDto);
        Optional<User> userOptional=userRepository.findById(id);
        if (userOptional.isEmpty()) {
            log.error(String.format("User Not found with Id  :{0}" , id));
            throw new EntityNotFoundException("User Not found with Id  :" + id);
        }
        User user=userOptional.get();
        patient.setUser(user);
        patientDAO.create(patient);
        return toDto(patient);
    }

    public List<PatientResponseDto> getAll() {
        List<Patient> patientList= patientDAO.getAll();
        return toDto(patientList);
    }


    public PatientResponseDto getOneById(UUID id) {
        Patient patientOptional= patientDAO.getOneById(id);
        return toDto(patientOptional);
    }

    public void delete(UUID id) {
        patientDAO.delete(id);
    }

    public PatientResponseDto update(PatientRequestDto patientRequestDto, UUID id) {
        // String mobileNumber=appointmentDAO.getUsernameFromJWT();
        //User user=userRepository.findByMobile(mobileNumber).orElseThrow(()->new EntityNotFoundException(mobileNumber));
        Patient patient=patientDAO.getOneById(id);
        patient.setAge(patientRequestDto.getAge());
        patient.setGender(Patient.Gender.valueOf(patientRequestDto.getGender()));
        patient.setBloodGroup(patientRequestDto.getBloodGroup());
        patientDAO.update(patient);
        return  toDto(patient);
    }


    private PatientResponseDto toDto(Patient patient){
        PatientResponseDto dto=new PatientResponseDto();
        User user=patient.getUser();
        dto.setName(user.getFirstName()+" "+user.getLastName());
        dto.setMobileNumber(user.getMobile());
        dto.setGender(patient.getGender().toString());
        dto.setBloodGroup(patient.getBloodGroup());
        return dto;
    }
    public List<PatientResponseDto> toDto(List<Patient> patients){
        return  patients.stream()
                .map(this::toDto)
                .toList();
    }
    private Patient toEntity(PatientRequestDto dto){
        Patient patient=new Patient();
        patient.setAge(dto.getAge());
        patient.setGender(Patient.Gender.valueOf(dto.getGender().toUpperCase()));
        patient.setBloodGroup(dto.getBloodGroup());
        String mobileNumber= appointmentService.getUsernameFromJWT();
        User user=userRepository.findByMobile(mobileNumber).orElseThrow(()->new EntityNotFoundException(mobileNumber));
        patient.setUser(user);
        return patient;

    }

}
