package com.hcltech.doctorpatient.dao.service;
import com.hcltech.doctorpatient.dao.service.DiseaseDao;
import com.hcltech.doctorpatient.dto.DiseaseDTO;
import com.hcltech.doctorpatient.exception.DiseaseNotFoundException;
import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
import com.hcltech.doctorpatient.model.Disease;
import com.hcltech.doctorpatient.model.Specialization;
import com.hcltech.doctorpatient.repository.DiseaseRepository;
import com.hcltech.doctorpatient.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiseaseDao {
    private final DiseaseRepository diseaseRepository;
    private final SpecializationRepository specializationRepository;

    public DiseaseDao(DiseaseRepository diseaseRepository, SpecializationRepository specializationRepository) {
        this.diseaseRepository = diseaseRepository;
        this.specializationRepository = specializationRepository;
    }

    public List<Disease> getall(){
        return diseaseRepository.findAll();
    }

    public Disease getById(UUID id){
        return diseaseRepository.findById(id).orElseThrow(()->new DiseaseNotFoundException("disease not found : "+id));
    }

    public Disease create(Disease disease){
        return diseaseRepository.save(disease);
    }

    public void delete(UUID id) {
        diseaseRepository.deleteById(id);
    }

    public Specialization getSpecializatioByName(String name) {
        return specializationRepository.findBySpecializationName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with name " + name));
    }
}