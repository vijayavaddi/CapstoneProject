package com.hcltech.doctorpatient.controller;


import com.hcltech.doctorpatient.dto.SpecializationDto;
import com.hcltech.doctorpatient.service.SpecializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctorPatientService/v1/specializations")
/*@SecurityRequirement(name = "Authorization")*/

public class SpecializationController {
    private static final Logger log = LoggerFactory.getLogger(SpecializationController.class);
    private SpecializationService specializationService;
    @Autowired
    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @GetMapping
    public ResponseEntity<List<SpecializationDto>> getAll(){
        List<SpecializationDto> result = specializationService.getAll();
        log.info("All the specialization retrieve");
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationDto> getById(@PathVariable("id") UUID id){
        SpecializationDto result = specializationService.getOneById(id);
        log.info("Retrieve Specialization\n"+result.toString());
        return ResponseEntity.ok(result);
    }
    @PostMapping
    public ResponseEntity<SpecializationDto> create(SpecializationDto specializationDto){
        SpecializationDto result = specializationService.create(specializationDto);
        log.info("Specialization created successfully\n"+result.toString());
        return new ResponseEntity(result,HttpStatus.CREATED);
    }
    @PutMapping
    public ResponseEntity<SpecializationDto> update(SpecializationDto specializationDto){
        SpecializationDto result = specializationService.update(specializationDto);
        log.info("Specialization updated successfully"+result.toString());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id){
        specializationService.delete(id);
        log.info("Specialization deleted successfully");
        return null;
    }

}
