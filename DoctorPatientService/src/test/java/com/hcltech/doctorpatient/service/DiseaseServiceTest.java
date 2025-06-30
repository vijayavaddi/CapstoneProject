package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.DiseaseDao;
import com.hcltech.doctorpatient.dto.DiseaseDTO;
import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
import com.hcltech.doctorpatient.model.Disease;
import com.hcltech.doctorpatient.model.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiseaseServiceTest {

    private DiseaseDao diseaseDao;
    private DiseaseService diseaseService;

    @BeforeEach
    void setUp() {
        diseaseDao = mock(DiseaseDao.class);
        diseaseService = new DiseaseService(diseaseDao);
    }

    @Test
    void testCreateDisease() {
        DiseaseDTO dto = new DiseaseDTO();
        dto.setDiseaseName("Flu");
        dto.setSpecializatonName("General");

        Specialization specialization = new Specialization();
        specialization.setSpecializationName("General");

        Disease disease = new Disease();
        disease.setName("Flu");
        disease.setSpecialization(specialization);

        Disease savedDisease = new Disease();
        savedDisease.setDiseaseId(UUID.randomUUID());
        savedDisease.setName("Flu");
        savedDisease.setSpecialization(specialization);

        when(diseaseDao.getSpecializatioByName("General")).thenReturn(specialization);
        when(diseaseDao.create(any(Disease.class))).thenReturn(savedDisease);

        DiseaseDTO result = diseaseService.createDisease(dto);

        assertNotNull(result);
        assertEquals("Flu", result.getDiseaseName());
        assertEquals("General", result.getSpecializatonName());
        verify(diseaseDao).getSpecializatioByName("General");
        verify(diseaseDao).create(any(Disease.class));
    }

    @Test
    void testGetDiseaseById() {
        UUID id = UUID.randomUUID();
        Specialization specialization = new Specialization();
        specialization.setSpecializationName("Cardiology");

        Disease disease = new Disease();
        disease.setDiseaseId(id);
        disease.setName("Heart Disease");
        disease.setSpecialization(specialization);

        when(diseaseDao.getById(id)).thenReturn(disease);

        DiseaseDTO result = diseaseService.getDiseaseById(id);

        assertNotNull(result);
        assertEquals(id, result.getDiseaseId());
        assertEquals("Heart Disease", result.getDiseaseName());
        assertEquals("Cardiology", result.getSpecializatonName());
        verify(diseaseDao).getById(id);
    }

    @Test
    void testGetAllDiseases() {
        Specialization specialization = new Specialization();
        specialization.setSpecializationName("Neuro");

        Disease disease = new Disease();
        disease.setDiseaseId(UUID.randomUUID());
        disease.setName("Migraine");
        disease.setSpecialization(specialization);

        when(diseaseDao.getall()).thenReturn(List.of(disease));

        List<DiseaseDTO> result = diseaseService.getAllDiseases();

        assertEquals(1, result.size());
        assertEquals("Migraine", result.get(0).getDiseaseName());
        assertEquals("Neuro", result.get(0).getSpecializatonName());
        verify(diseaseDao).getall();
    }

    @Test
    void testDeleteDisease() {
        UUID id = UUID.randomUUID();
        doNothing().when(diseaseDao).delete(id);

        diseaseService.deleteDisease(id);

        verify(diseaseDao).delete(id);
    }

    @Test
    void testCreateDisease_SpecializationNotFound() {
        DiseaseDTO dto = new DiseaseDTO();
        dto.setDiseaseName("Unknown");
        dto.setSpecializatonName("UnknownSpec");

        when(diseaseDao.getSpecializatioByName("UnknownSpec"))
                .thenThrow(new ResourceNotFoundException("Specialization not found"));

        assertThrows(ResourceNotFoundException.class, () -> diseaseService.createDisease(dto));
        verify(diseaseDao).getSpecializatioByName("UnknownSpec");
    }
}