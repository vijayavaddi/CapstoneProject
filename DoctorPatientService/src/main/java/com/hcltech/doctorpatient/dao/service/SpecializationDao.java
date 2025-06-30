package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.model.Specialization;
import com.hcltech.doctorpatient.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SpecializationDao {

    private SpecializationRepository specializationRepository;
    @Autowired
    public SpecializationDao(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }
    public List<Specialization> getAll(){
        return specializationRepository.findAll();
    }
    public Specialization getOneById(UUID id){
        return specializationRepository.getById(id);
    }
    public Specialization create(Specialization specialization){
        return specializationRepository.save(specialization);
    }
    public Specialization update(Specialization specialization){
        Optional<Specialization> findById = specializationRepository.findById(specialization.getId());

        if (findById.isPresent()){
            Specialization existingSpecialization =findById.get();
            existingSpecialization.setSpecializationName(specialization.getSpecializationName());
            return  specializationRepository.save(existingSpecialization);
        }else {
            return null;
        }

    }
    public  void delete(UUID id){
        specializationRepository.deleteById(id);
    }
}
