package com.hcltech.doctorpatient.service;


import com.hcltech.doctorpatient.dao.service.SpecializationDao;
import com.hcltech.doctorpatient.dto.SpecializationDto;
import com.hcltech.doctorpatient.exception.EntityNotFoundException;
import com.hcltech.doctorpatient.model.Specialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpecializationService {
    private SpecializationDao specializationDao;
    @Autowired
    public SpecializationService(SpecializationDao specializationDao) {
        this.specializationDao = specializationDao;
    }
    public List<SpecializationDto> getAll() {
        List<Specialization> specializationList = specializationDao.getAll();
        return specializationDtoList(specializationList);
    }
    public SpecializationDto getOneById(UUID id) {
        Specialization specialization = specializationDao.getOneById(id);
        if (specialization == null) {
            throw new EntityNotFoundException("No specialization found by Id");
        }
       return toDto(specialization);
    }
    public SpecializationDto create(SpecializationDto specializationDto) {
        Specialization specialization = toEntity(specializationDto);
        Specialization savedSpecialization = specializationDao.create(specialization);
        return toDto(savedSpecialization);
    }
    public SpecializationDto update(SpecializationDto specializationDto) {
        if (specializationDto == null || specializationDto.getId() == null) {
            throw new IllegalArgumentException("Specialization ID must not be null");
        }
        Specialization specialization = toEntity(specializationDto);
        Specialization updatedSpecialization = specializationDao.update(specialization);
        return toDto(updatedSpecialization);
    }

    public void delete(UUID id) {
        specializationDao.delete(id);
    }
    public List<SpecializationDto> specializationDtoList(List<Specialization> specializations) {
        return specializations.stream()
                .map(specialization -> toDto(specialization))
                .collect(Collectors.toList());
    }
    public SpecializationDto toDto(Specialization specialization) {
        SpecializationDto result = new SpecializationDto();
        result.setId(specialization.getId());
        result.setSpecialization(specialization.getSpecializationName());
        return result;
    }
    public Specialization toEntity(SpecializationDto specializationDto) {
        Specialization result = new Specialization();
        result.setId(specializationDto.getId());
        result.setSpecializationName(specializationDto.getSpecialization());
        return result;
    }
}
