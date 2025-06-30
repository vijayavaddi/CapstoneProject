package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dto.doctordto.DoctorRequestDto;
import com.hcltech.doctorpatient.dto.doctordto.DoctorResponseDto;
import com.hcltech.doctorpatient.service.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<DoctorResponseDto>> getAll() {
        log.info("Fetching all doctors");
        List<DoctorResponseDto> doctors = doctorService.getAll();
        log.debug("Total doctors fetched: {}", doctors.size());
        return ResponseEntity.ok(doctors);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorResponseDto> getOneById(@PathVariable UUID doctorId) {
        log.info("Fetching doctor by ID: {}", doctorId);
        DoctorResponseDto doctor = doctorService.getOneById(doctorId);
        log.debug("Doctor details retrieved: {}", doctor);
        return ResponseEntity.ok(doctor);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody DoctorRequestDto doctor, @RequestParam UUID userId) {
        log.info("Creating doctor profile for userId: {}", userId);
        UUID doctorId = doctorService.create(doctor, userId);
        log.debug("Doctor created with ID: {}", doctorId);
        URI location = URI.create("/doctors/" + doctorId);
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String> delete(@PathVariable UUID doctorId) {
        log.info("Deleting doctor with ID: {}", doctorId);
        doctorService .delete(doctorId);
        log.debug("Doctor deleted successfully: {}", doctorId);
        return ResponseEntity.ok("Doctor deleted successfully: " + doctorId);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorResponseDto> update(@RequestBody DoctorRequestDto doctorRequestDto, @PathVariable UUID doctorId) {
        log.info("Updating doctor with ID: {}", doctorId);
        DoctorResponseDto updatedDoctor = doctorService.update(doctorRequestDto, doctorId);
        log.debug("Doctor updated successfully: {}", updatedDoctor);
        return ResponseEntity.ok(updatedDoctor);
    }
}
