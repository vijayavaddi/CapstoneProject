package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dto.appointmentdto.DoctorAppointmentsDto;
import com.hcltech.doctorpatient.service.AppointmentService;
import com.hcltech.doctorpatient.dto.appointmentdto.AppointmentRequestDto;
import com.hcltech.doctorpatient.dto.appointmentdto.AppointmentResponseDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/appointment")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @PostMapping("/book")
    public ResponseEntity<AppointmentResponseDto> create(@Valid @RequestBody AppointmentRequestDto appointmentRequestDto){
        logger.info("Booking appointment for patientId: {}", appointmentRequestDto.getMobile());
        AppointmentResponseDto result = appointmentService.create(appointmentRequestDto);
        logger.debug("Appointment booked successfully: {}", result);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @PutMapping("/reschedule/{id}")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable UUID id, @Valid @RequestBody AppointmentRequestDto appointmentRequestDto){
        logger.info("Rescheduling appointment with ID: {}", id);
        AppointmentResponseDto result = appointmentService.update(id, appointmentRequestDto);
        logger.debug("Appointment rescheduled: {}", result);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','DOCTOR')")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getall(){
        logger.info("Fetching all appointments");
        List<AppointmentResponseDto> appointments = appointmentService.getall();
        logger.debug("Total appointments fetched: {}", appointments.size());
        return ResponseEntity.ok(appointments);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @GetMapping("/getbyid")
    public ResponseEntity<AppointmentResponseDto> getbyId(UUID id){
        logger.info("Fetching appointment by ID: {}", id);
        AppointmentResponseDto appointment = appointmentService.getonebyId(id);
        logger.debug("Appointment details: {}", appointment);
        return ResponseEntity.ok(appointment);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @PostMapping("/cancel")
    public ResponseEntity<AppointmentResponseDto> cancel(UUID id){
        logger.info("Cancelling appointment with ID: {}", id);
        AppointmentResponseDto cancelled = appointmentService.delete(id);
        logger.debug("Appointment cancelled: {}", cancelled);
        return ResponseEntity.ok(cancelled);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> markAsCompleted(@PathVariable UUID id) {
        logger.info("Marking appointment as completed for ID: {}", id);
        AppointmentResponseDto updated = appointmentService.markAsCompleted(id);
        logger.debug("Appointment marked as completed: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping("/my-appointments")
    public ResponseEntity<List<DoctorAppointmentsDto>> getMyAppointments() {
        logger.info("Fetching appointments for current doctor");
        List<DoctorAppointmentsDto> appointments = appointmentService.getMyAppointments();
        logger.debug("Doctor's appointments fetched: {}", appointments.size());
        return ResponseEntity.ok(appointments);
    }
}
