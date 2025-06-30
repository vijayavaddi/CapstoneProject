package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.exception.DiseaseNotFoundException;
import com.hcltech.doctorpatient.exception.ResourceNotFoundException;
import com.hcltech.doctorpatient.model.Disease;
import com.hcltech.doctorpatient.model.Specialization;
import com.hcltech.doctorpatient.repository.DiseaseRepository;
import com.hcltech.doctorpatient.repository.SpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiseaseDaoTest {

    private DiseaseRepository diseaseRepository;
    private SpecializationRepository specializationRepository;
    private DiseaseDao diseaseDao;

    @BeforeEach
    void setUp() {
        diseaseRepository = mock(DiseaseRepository.class);
        specializationRepository = mock(SpecializationRepository.class);
        diseaseDao = new DiseaseDao(diseaseRepository, specializationRepository);
    }

    @Test
    void testGetAll() {
        List<Disease> diseases = Arrays.asList(new Disease(), new Disease());
        when(diseaseRepository.findAll()).thenReturn(diseases);

        List<Disease> result = diseaseDao.getall();
        assertEquals(2, result.size());
        verify(diseaseRepository).findAll();
    }

    @Test
    void testGetById_Found() {
        UUID id = UUID.randomUUID();
        Disease disease = new Disease();
        when(diseaseRepository.findById(id)).thenReturn(Optional.of(disease));

        Disease result = diseaseDao.getById(id);
        assertEquals(disease, result);
        verify(diseaseRepository).findById(id);
    }

    @Test
    void testGetById_NotFound() {
        UUID id = UUID.randomUUID();
        when(diseaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DiseaseNotFoundException.class, () -> diseaseDao.getById(id));
        verify(diseaseRepository).findById(id);
    }

    @Test
    void testCreate() {
        Disease disease = new Disease();
        when(diseaseRepository.save(disease)).thenReturn(disease);

        Disease result = diseaseDao.create(disease);
        assertEquals(disease, result);
        verify(diseaseRepository).save(disease);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        doNothing().when(diseaseRepository).deleteById(id);

        diseaseDao.delete(id);
        verify(diseaseRepository).deleteById(id);
    }

    @Test
    void testGetSpecializatioByName_Found() {
        String name = "Cardiology";
        Specialization specialization = new Specialization();
        when(specializationRepository.findBySpecializationName(name)).thenReturn(Optional.of(specialization));

        Specialization result = diseaseDao.getSpecializatioByName(name);
        assertEquals(specialization, result);
        verify(specializationRepository).findBySpecializationName(name);
    }

    @Test
    void testGetSpecializatioByName_NotFound() {
        String name = "Unknown";
        when(specializationRepository.findBySpecializationName(name)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> diseaseDao.getSpecializatioByName(name));
        verify(specializationRepository).findBySpecializationName(name);
    }
}