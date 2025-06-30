package com.hcltech.doctorpatient.service;


import com.hcltech.doctorpatient.dao.service.SpecializationDao;
import com.hcltech.doctorpatient.dto.SpecializationDto;
import com.hcltech.doctorpatient.exception.EntityNotFoundException;
import com.hcltech.doctorpatient.model.Specialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecializationServiceTest {
    private SpecializationDao specializationDao;
    private SpecializationService specializationService;

    @BeforeEach
    void setUp() {
        specializationDao = mock(SpecializationDao.class);
        specializationService = new SpecializationService(specializationDao);
    }

    @Test
    void testGetAll() {
        Specialization specialization = new Specialization();
        specialization.setId(UUID.randomUUID());
        specialization.setSpecializationName("Cardiology");
        when(specializationDao.getAll()).thenReturn(List.of(specialization));
        List<SpecializationDto> result = specializationService.getAll();
        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getSpecialization());
        verify(specializationDao, times(1)).getAll();
    }

    @Test
    void testGetOneById_found() {
        UUID id = UUID.randomUUID();
        Specialization specialization = new Specialization();
        specialization.setId(id);
        specialization.setSpecializationName("Neurology");
        when(specializationDao.getOneById(id)).thenReturn(specialization);
        SpecializationDto result = specializationService.getOneById(id);
        assertNotNull(result);
        assertEquals("Neurology", result.getSpecialization());
        verify(specializationDao, times(1)).getOneById(id);
    }

    @Test
    void testGetOneById_notFound() {
        UUID id = UUID.randomUUID();
        when(specializationDao.getOneById(id)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> specializationService.getOneById(id));
    }

    @Test
    void testCreate() {
        SpecializationDto dto = new SpecializationDto();
        dto.setSpecialization("Dermatology");
        Specialization specialization = new Specialization();
        specialization.setId(UUID.randomUUID());
        specialization.setSpecializationName("Dermatology");
        when(specializationDao.create(any(Specialization.class))).thenReturn(specialization);
        SpecializationDto result = specializationService.create(dto);
        assertNotNull(result);
        assertEquals("Dermatology", result.getSpecialization());
        verify(specializationDao, times(1)).create(any(Specialization.class));
    }

    @Test
    void testUpdate_valid() {
        UUID id = UUID.randomUUID();
        SpecializationDto dto = new SpecializationDto();
        dto.setId(id);
        dto.setSpecialization("Orthopedics");
        Specialization specialization = new Specialization();
        specialization.setId(id);
        specialization.setSpecializationName("Orthopedics");
        when(specializationDao.update(any(Specialization.class))).thenReturn(specialization);
        SpecializationDto result = specializationService.update(dto);
        assertNotNull(result);
        assertEquals("Orthopedics", result.getSpecialization());
        verify(specializationDao, times(1)).update(any(Specialization.class));
    }
    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        specializationService.delete(id);
        verify(specializationDao, times(1)).delete(id);
    }
}