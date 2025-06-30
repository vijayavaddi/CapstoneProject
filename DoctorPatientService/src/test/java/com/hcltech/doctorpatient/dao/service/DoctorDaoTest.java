package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.model.Doctor;
import com.hcltech.doctorpatient.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorDaoTest {

    private DoctorRepository doctorRepository;
    private DoctorDao doctorDao;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        doctorRepository = mock(DoctorRepository.class);
        doctorDao = new DoctorDao();
        // Inject mock via reflection since field is private and @Autowired
        var field = DoctorDao.class.getDeclaredField("doctorRepository");
        field.setAccessible(true);
        try {
            field.set(doctorDao, doctorRepository);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAll() {
        List<Doctor> doctors = Arrays.asList(new Doctor(), new Doctor());
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = doctorDao.getAll();

        assertEquals(2, result.size());
        verify(doctorRepository).findAll();
    }

    @Test
    void testCreate() {
        Doctor doctor = new Doctor();
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor result = doctorDao.create(doctor);

        assertEquals(doctor, result);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void testGetOneById() {
        UUID id = UUID.randomUUID();
        Doctor doctor = new Doctor();
        when(doctorRepository.getById(id)).thenReturn(doctor);

        Doctor result = doctorDao.getOneById(id);

        assertEquals(doctor, result);
        verify(doctorRepository).getById(id);
    }

    @Test
    void testDeleteOneById() {
        UUID id = UUID.randomUUID();
        doNothing().when(doctorRepository).deleteById(id);

        doctorDao.deleteOneById(id);

        verify(doctorRepository).deleteById(id);
    }

    @Test
    void testUpdate_DoctorExists() {
        UUID id = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setDoctorId(id);

        Doctor existing = new Doctor();
        existing.setDoctorId(id);

        when(doctorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(existing)).thenReturn(existing);

        Doctor result = doctorDao.update(doctor);

        assertNotNull(result);
        verify(doctorRepository).findById(id);
        verify(doctorRepository).save(existing);
    }

    @Test
    void testUpdate_DoctorNotExists() {
        UUID id = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setDoctorId(id);

        when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        Doctor result = doctorDao.update(doctor);

        assertNull(result);
        verify(doctorRepository).findById(id);
        verify(doctorRepository, never()).save(any());
    }
}