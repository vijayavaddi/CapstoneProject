package com.hcltech.doctorpatient.service;


import com.hcltech.doctorpatient.dao.service.DiseaseDao;
import com.hcltech.doctorpatient.dto.DiseaseDTO;
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
public class DiseaseService {
    private final DiseaseDao diseaseDao;

    public DiseaseService(DiseaseDao diseaseDAO) {
        this.diseaseDao = diseaseDAO;
    }

    public DiseaseDTO createDisease(DiseaseDTO dto) {
        Specialization specialization = diseaseDao.getSpecializatioByName(dto.getSpecializatonName());
        Disease disease = new Disease();
        disease.setName(dto.getDiseaseName());
        disease.setSpecialization(specialization);
        Disease saved = diseaseDao.create(disease);
        return convertToDto(saved);
    }

    public DiseaseDTO getDiseaseById(UUID id) {
        Disease disease =diseaseDao.getById(id);
        return convertToDto(disease);
    }

    public List<DiseaseDTO> getAllDiseases() {
        return diseaseDao.getall()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteDisease(UUID id) {
        diseaseDao.delete(id);
    }

    private DiseaseDTO convertToDto(Disease disease) {
        DiseaseDTO dto = new DiseaseDTO();
        dto.setDiseaseId(disease.getDiseaseId());
        dto.setDiseaseName(disease.getName());
        dto.setSpecializatonName(disease.getSpecialization().getSpecializationName());
        return dto;
    }
}

