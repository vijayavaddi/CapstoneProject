package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.controller.SpecializationController;
import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.dao.service.DoctorDao;
import com.hcltech.doctorpatient.dto.doctordto.DoctorRequestDto;
import com.hcltech.doctorpatient.dto.doctordto.DoctorResponseDto;
import com.hcltech.doctorpatient.model.Doctor;
import com.hcltech.doctorpatient.model.Specialization;
import com.hcltech.doctorpatient.model.User;
import com.hcltech.doctorpatient.repository.DoctorRepository;
import com.hcltech.doctorpatient.repository.SpecializationRepository;
import com.hcltech.doctorpatient.repository.UserRepository;
import com.hcltech.doctorpatient.service.DoctorService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DoctorService {
    private static final Logger log = LoggerFactory.getLogger(SpecializationController.class);

   private final DoctorDao doctorDao;
   private final ModelMapper modelMapper;
   private final SpecializationRepository specializationRepository;
   private final UserRepository userRepository;
   private final AppointmentService appointmentService;

    public DoctorService(DoctorDao doctorDao, ModelMapper modelMapper, SpecializationRepository specializationRepository, UserRepository userRepository, AppointmentService appointmentService) {
        this.doctorDao = doctorDao;
        this.modelMapper = modelMapper;
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
    }

    public List<DoctorResponseDto> getAll() {
        List<Doctor> doctors = doctorDao.getAll();
        if(doctors.isEmpty()){
            throw new EntityNotFoundException("Record Not found exception");
        }
        return toDto(doctors);
    }

    public UUID create(DoctorRequestDto doctorRequestDto, UUID userId) {
        Doctor doctor = toEntity(doctorRequestDto);
        Specialization specialization = specializationRepository.getBySpecializationName(doctorRequestDto.getSpecialist());
        if (specialization == null) {
            log.error("Specialization Not found");
            throw new EntityNotFoundException("Specialization Not found");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("User Not found with Id  :" + userId);
            throw new EntityNotFoundException("User Not found with Id  :" + userId);
        }
        User existingUser = user.get();
        doctor.setUser(existingUser);
        doctor.setSpecialization(specialization);
        doctorDao.create(doctor);
        existingUser.setDoctor(doctor);
        userRepository.save(existingUser);
        return doctor.getDoctorId();
    }

    public DoctorResponseDto getOneById(UUID doctorId) {
        Doctor doctor = doctorDao.getOneById(doctorId);
        if (doctor ==null) {
            log.error("Doctor not found at Id: " + doctorId);
            throw new EntityNotFoundException("Doctor not found with id; " + doctorId);
        }
        Doctor result = new Doctor();
        return toDto(result);
    }
    public void delete(UUID doctorId) {
        if (doctorDao.getOneById(doctorId)==null) {
            throw new EntityNotFoundException("Doctor not found with ID: " + doctorId);
        }
        doctorDao.deleteOneById(doctorId);
    }
    public DoctorResponseDto update(DoctorRequestDto doctorRequestDto,UUID userId) {
    Doctor doctor=doctorDao.getOneById(userId);
        Specialization specialization = specializationRepository.getBySpecializationName(doctorRequestDto.getSpecialist());
        if(specialization != null) {
            doctor.setSpecialization(specialization);
        }
        doctor.setExperience(doctorRequestDto.getExperience());
        doctor.setQualification(doctorRequestDto.getQualification());
        doctorDao.update(doctor);
        return toDto(doctor);

    }


    private List<DoctorResponseDto> toDto(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::toDto)
                .toList();
    }

    private DoctorResponseDto toDto(Doctor doctor) {
        DoctorResponseDto dto = modelMapper.map(doctor, DoctorResponseDto.class);
        User user = doctor.getUser();
        if (user != null) {
            dto.setName(user.getFirstName() + " " + user.getLastName());
            dto.setMobileNumber(user.getMobile());
        }

        Specialization specialization = doctor.getSpecialization();
        if (specialization != null) {
            dto.setSpecialist(specialization.getSpecializationName());
        }
        return dto;
    }


    private Doctor toEntity(DoctorRequestDto doctorRequestDto) {
        Doctor doctor=new Doctor();
        Specialization specialization=specializationRepository.getBySpecializationName(doctorRequestDto.getSpecialist());
        doctor.setSpecialization(specialization);
        doctor.setExperience(doctorRequestDto.getExperience());
        doctor.setQualification(doctorRequestDto.getQualification());
        String mobileNumber=appointmentService.getUsernameFromJWT();
        User user=userRepository.findByMobile(mobileNumber).orElseThrow(()->new EntityNotFoundException(mobileNumber));
        doctor.setUser(user);
        return doctor;
    }


}

