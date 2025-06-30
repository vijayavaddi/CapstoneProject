package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.model.Specialization;
import com.hcltech.doctorpatient.repository.SpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecializationDaoTest {

    private SpecializationRepository specializationRepository;
    private SpecializationDao specializationDao;

    @BeforeEach
    void setUp() {
        specializationRepository = mock(SpecializationRepository.class);
        specializationDao = new SpecializationDao(specializationRepository);
    }

    @Test
    void testGetAll() {
        List<Specialization> mockList = List.of(new Specialization(), new Specialization());
        when(specializationRepository.findAll()).thenReturn(mockList);

        List<Specialization> result = specializationDao.getAll();

        assertEquals(2, result.size());
        verify(specializationRepository, times(1)).findAll();
    }

    @Test
    void testGetOneById() {
        UUID id = UUID.randomUUID();
        Specialization specialization = new Specialization();
        when(specializationRepository.getById(id)).thenReturn(specialization);

        Specialization result = specializationDao.getOneById(id);

        assertNotNull(result);
        verify(specializationRepository, times(1)).getById(id);
    }

    @Test
    void testCreate() {
        Specialization specialization = new Specialization();
        when(specializationRepository.save(specialization)).thenReturn(specialization);

        Specialization result = specializationDao.create(specialization);

        assertEquals(specialization, result);
        verify(specializationRepository, times(1)).save(specialization);
    }

    @Test
    void testUpdate_whenExists() {
        UUID id = UUID.randomUUID();
        Specialization specialization = new Specialization();
        specialization.setId(id);
        specialization.setSpecializationName("Cardiology");

        Specialization existing = new Specialization();
        existing.setId(id);
        existing.setSpecializationName("Old Name");

        when(specializationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(specializationRepository.save(any(Specialization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Specialization result = specializationDao.update(specialization);

        assertNotNull(result);
        assertEquals("Cardiology", result.getSpecializationName());
        verify(specializationRepository).save(existing);
    }

    @Test
    void testUpdate_whenNotExists() {
        UUID id = UUID.randomUUID();
        Specialization specialization = new Specialization();
        specialization.setId(id);

        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        Specialization result = specializationDao.update(specialization);

        assertNull(result);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();

        specializationDao.delete(id);

        verify(specializationRepository, times(1)).deleteById(id);
    }
}