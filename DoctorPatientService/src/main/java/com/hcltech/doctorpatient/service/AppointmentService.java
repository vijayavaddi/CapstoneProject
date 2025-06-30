package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.AppointmentDao;
import com.hcltech.doctorpatient.dao.service.AuthenticationService;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentDao appointmentDao;
    private final DiseaseRepository diseaseRepository;
    private final DoctorRepository doctorRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    public AppointmentResponseDto create(AppointmentRequestDto appointmentRequestDto){
        Appointment appointment=toEntity(appointmentRequestDto);
        appointmentDao.create(appointment);
        return toDto(appointment);

    }
    public AppointmentResponseDto update(UUID id,AppointmentRequestDto appointmentRequestDto){
        Appointment appointment=toUpdateEntity(id,appointmentRequestDto);
        appointmentDao.update(appointment);
        return toDto(appointment);

    }
    public List<AppointmentResponseDto> getall(){
        List<Appointment> appointments= appointmentDao.getall();
        return toDto(appointments);

    }
    public AppointmentResponseDto getonebyId(UUID id){
        Appointment appointment= appointmentDao.getById(id);
        return toDto(appointment);

    }
    public AppointmentResponseDto delete(UUID id){
        Appointment appointment= appointmentDao.getById(id);
        appointment.setStatus(Appointment.Status.CANCELLED);
        appointmentDao.create(appointment);
        return toDto(appointment);

    }
    public List<DoctorAppointmentsDto> getMyAppointments(){
        String mobileNumber= getUsernameFromJWT();
        User user=userRepository.findByMobile(mobileNumber).orElseThrow(()->new EntityNotFoundException("User not found"));
        List<Appointment> appointments= appointmentDao.getMyAppointmets(user.getId());
        return toDoctorDto(appointments);
    }
    public AppointmentResponseDto markAsCompleted(UUID id) {
        Appointment appointment= appointmentDao.markAsCompleted(id);
        return toDto(appointment);
    }

    public List<AppointmentResponseDto> toDto(List<Appointment> appointments){
        return appointments.stream()
                .map(this::toDto)
                .toList();

    }
    public AppointmentResponseDto toDto(Appointment appointment){
        AppointmentResponseDto appointmentResponseDto=new AppointmentResponseDto();
        appointmentResponseDto.setDiseaseName(appointment.getDisease().getName());
        UUID doctorid=appointment.getDoctor().getDoctorId();
        User doctor=userRepository.findById(doctorid).orElseThrow(()->new EntityNotFoundException("Doctor not found"));
        appointmentResponseDto.setDoctorName(doctor.getFirstName()+" "+doctor.getLastName());
        appointmentResponseDto.setStatus(appointment.getStatus().toString());
        //patient id need to get from jwt
        String mobileNumber= getUsernameFromJWT();
        User user=userRepository.findByMobile(mobileNumber).orElseThrow(()->new EntityNotFoundException("Not found"));
        appointmentResponseDto.setPatientName(user.getFirstName()+" "+user.getLastName());
        appointmentResponseDto.setFromTime(appointment.getFromTime());
        appointmentResponseDto.setToTime(appointment.getToTime());
        return appointmentResponseDto;
    }
    public Appointment toEntity(AppointmentRequestDto dto) {
        String mobileNumber=getUsernameFromJWT();
        User user1 = userRepository.findByMobile(mobileNumber).orElseThrow(() -> new EntityNotFoundException("user not found"));
        Patient patient = patientRepository.findById(user1.getId()).orElseThrow(() -> new EntityNotFoundException("patient not found"));
        List<Appointment> patientappointments = appointmentDao.getall();
        for (Appointment appointment : patientappointments) {
            if (appointment.getPatient() == patient && appointment.getStatus() == Appointment.Status.SCHEDULED) {
                throw new DuplicateAppointmentException("Already booked a slot");
            }
        }
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.Status.SCHEDULED);

        appointment.setFromTime(dto.getFromTime());
        appointment.setToTime(dto.getToTime());
        appointment.setDescription(dto.getDescription());
        User user=userRepository.findByMobile(dto.getMobile()).orElseThrow(()->new EntityNotFoundException("user Not found"));
        Patient patient1=patientRepository.findById(user.getId()).orElseThrow(()->new EntityNotFoundException("Patient Not found"));
        appointment.setPatient(patient1);
        //  Fetch disease and specialization
        Disease disease = diseaseRepository.findByName(dto.getDiseasename());

        if (disease == null) {
            throw new DiseaseNotFoundException("Disease not found: " + dto.getDiseasename());
        }
        appointment.setDisease(disease);

        UUID specializationId = disease.getSpecialization().getId();

        //  Find doctors by specialization
        List<Doctor> doctors = doctorRepository.findBySpecializationId(specializationId);
        if (doctors == null || doctors.isEmpty()) {
            throw new DoctorNotAvailableException("No doctors available for specialization: " + specializationId);
        }

        // Check for available doctor
        Doctor assignedDoctor = null;
        for (Doctor doctor : doctors) {
            List<Appointment> appointments = appointmentDao.getMyAppointmets(doctor.getDoctorId());

            // Filter appointments for the same day
            List<Appointment> sameDayAppointments = appointments.stream()
                    .filter(a->a.getStatus()==Appointment.Status.SCHEDULED)
                    .filter(a -> a.getFromTime().toLocalDate().equals(dto.getFromTime().toLocalDate()))
                    .toList();

            // Check if doctor has less than 4 appointments that day
            if (sameDayAppointments.size() < 4) {
                boolean hasConflict = sameDayAppointments.stream().anyMatch(existing ->
                        dto.getFromTime().isBefore(existing.getToTime()) &&
                                dto.getToTime().isAfter(existing.getFromTime())
                );

                if (!hasConflict) {
                    assignedDoctor = doctor;
                    break;
                }
            }
        }

        if (assignedDoctor == null) {
            throw new DoctorNotAvailableException("No available doctors for the selected time slot. Please choose another slot.");
        }
        appointment.setDoctor(assignedDoctor);
        return appointment;
    }
    private Appointment toUpdateEntity(UUID appointmentId, AppointmentRequestDto dto) {
        Appointment appointment = appointmentDao.getById(appointmentId);

        appointment.setStatus(Appointment.Status.RESCHEDULED);

        updateBasicFields(appointment, dto);
        updatePatientIfNeeded(appointment, dto);
        boolean shouldReassignDoctor = updateDiseaseIfNeeded(appointment, dto) || hasTimeChanged(dto);

        if (shouldReassignDoctor) {
            reassignDoctor(appointment);
        }

        return appointment;
    }

    private void updateBasicFields(Appointment appointment, AppointmentRequestDto dto) {
        if (dto.getFromTime() != null) {
            appointment.setFromTime(dto.getFromTime());
        }
        if (dto.getToTime() != null) {
            appointment.setToTime(dto.getToTime());
        }
        if (dto.getDescription() != null) {
            appointment.setDescription(dto.getDescription());
        }
    }

    private void updatePatientIfNeeded(Appointment appointment, AppointmentRequestDto dto) {
        if (dto.getMobile() != null) {
            User user = userRepository.findByMobile(dto.getMobile())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Patient patient = patientRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
            appointment.setPatient(patient);
        }
    }

    private boolean updateDiseaseIfNeeded(Appointment appointment, AppointmentRequestDto dto) {
        if (dto.getDiseasename() != null) {
            Disease disease = diseaseRepository.findByName(dto.getDiseasename());
            if (disease == null) {
                throw new DoctorNotAvailableException("Disease not found: " + dto.getDiseasename());
            }
            appointment.setDisease(disease);
            return true;
        }
        return false;
    }

    private boolean hasTimeChanged(AppointmentRequestDto dto) {
        return dto.getFromTime() != null || dto.getToTime() != null;
    }

    private void reassignDoctor(Appointment appointment) {
        Disease disease = appointment.getDisease();
        UUID specializationId = disease.getSpecialization().getId();
        List<Doctor> doctors = doctorRepository.findBySpecializationId(specializationId);

        if (doctors == null || doctors.isEmpty()) {
            throw new DoctorNotAvailableException("No doctors available for specialization: " + specializationId);
        }

        Doctor assignedDoctor = findAvailableDoctor(doctors, appointment);
        if (assignedDoctor == null) {
            throw new DoctorNotAvailableException("No available doctors for the selected time slot. Please choose another slot.");
        }

        appointment.setDoctor(assignedDoctor);
    }

    private Doctor findAvailableDoctor(List<Doctor> doctors, Appointment appointment) {
        for (Doctor doctor : doctors) {
            List<Appointment> appointments = appointmentDao.getMyAppointmets(doctor.getDoctorId());

            List<Appointment> sameDayAppointments = appointments.stream()
                    .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
                    .filter(a -> a.getFromTime().toLocalDate().equals(appointment.getFromTime().toLocalDate()))
                    .toList();

            if (sameDayAppointments.size() < 4) {
                boolean hasConflict = sameDayAppointments.stream().anyMatch(existing ->
                        appointment.getFromTime().isBefore(existing.getToTime()) &&
                                appointment.getToTime().isAfter(existing.getFromTime())
                );

                if (!hasConflict) {
                    return doctor;
                }
            }
        }
        return null;
    }
    public List<DoctorAppointmentsDto> toDoctorDto(List<Appointment> appointments) {
        return  appointments.stream()
                .map(this::toDoctorDto)
                .toList();
    }
    public DoctorAppointmentsDto toDoctorDto(Appointment appointment){
        DoctorAppointmentsDto doctorAppointmentsDto=new DoctorAppointmentsDto();
        doctorAppointmentsDto.setStatus(appointment.getStatus().toString());
        doctorAppointmentsDto.setToTime(appointment.getToTime());
        doctorAppointmentsDto.setFromTime(appointment.getFromTime());
        doctorAppointmentsDto.setDisease_name(appointment.getDisease().getName());
        User user=userRepository.findById(appointment.getPatient().getPatientId()).orElseThrow(()->new EntityNotFoundException("user not found"));
        doctorAppointmentsDto.setPatient_name(user.getFirstName()+" "+user.getLastName());
        return doctorAppointmentsDto;
    }


    public String getUsernameFromJWT(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader= request.getHeader("Authorization");
        String jwt = null;
        String mobileNumber = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer " prefix
            mobileNumber = jwtUtil.extractUsernameFromToken(jwt);
        } else {
            throw new JwtException("JWT Token is missing or invalid");
        }
        return mobileNumber;
    }

}
