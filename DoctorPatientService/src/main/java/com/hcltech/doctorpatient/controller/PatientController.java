package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dto.patientdto.PatientRequestDto;
import com.hcltech.doctorpatient.dto.patientdto.PatientResponseDto;
import com.hcltech.doctorpatient.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
/*@SecurityRequirement(name = "Authorization")*/
public class PatientController {
    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @PostMapping
    public ResponseEntity<PatientResponseDto> create(@RequestBody PatientRequestDto patientRequestDto, @RequestParam UUID id) {
        log.info("Creating patient with reference ID: {}", id);
        UUID createdId = patientService.create(patientRequestDto, id).getId();
        URI location = URI.create("/patients/" + createdId);
        log.info("Patient created with ID: {}", createdId);
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAll() {
        log.info("Fetching all patients");
        List<PatientResponseDto> patients = patientService.getAll();
        log.info("Total patients fetched: {}", patients.size());
        return ResponseEntity.ok(patients);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getOneById(@PathVariable UUID id) {
        log.info("Fetching patient with ID: {}", id);
        PatientResponseDto patient = patientService.getOneById(id);
        log.info("Patient fetched: {}", patient);
        return ResponseEntity.ok(patient);
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Deleting patient with ID: {}", id);
        patientService.delete(id);
        log.info("Patient deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> update(@RequestBody PatientRequestDto patientRequestDto, @PathVariable UUID id) {
        log.info("Updating patient with ID: {}", id);
        PatientResponseDto updated = patientService.update(patientRequestDto, id);
        log.info("Patient updated successfully: {}", updated);
        return ResponseEntity.ok(updated);
    }

}
