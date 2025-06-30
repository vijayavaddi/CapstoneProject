package com.hcltech.doctorpatient.controller;

import com.hcltech.doctorpatient.dto.DiseaseDTO;
import com.hcltech.doctorpatient.service.DiseaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
@Tag(name = "Disease Controller", description = "API's for managing diseases")
public class DiseaseController {

    private static final Logger logger = LoggerFactory.getLogger(DiseaseController.class);
    private final DiseaseService diseaseService;

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping
    public DiseaseDTO create(@Valid @RequestBody DiseaseDTO dto) {
        logger.info("Creating new disease entry: {}", dto.getDiseaseName());
        DiseaseDTO created = diseaseService.createDisease(dto);
        logger.debug("Disease created successfully: {}", created);
        return created;
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DiseaseDTO> getById(@PathVariable UUID id) {
        logger.info("Fetching disease by ID: {}", id);
        DiseaseDTO disease = diseaseService.getDiseaseById(id);
        logger.debug("Disease details retrieved: {}", disease);
        return ResponseEntity.ok(disease);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN','PATIENT')")
    @GetMapping
    public ResponseEntity<List<DiseaseDTO>> getAll() {
        logger.info("Fetching all diseases");
        List<DiseaseDTO> diseases = diseaseService.getAllDiseases();
        logger.debug("Total diseases fetched: {}", diseases.size());
        return ResponseEntity.ok(diseases);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logger.info("Deleting disease with ID: {}", id);
        diseaseService.deleteDisease(id);
        logger.debug("Disease deleted successfully for ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
