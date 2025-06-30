package com.hcltech.doctorpatient.repository;

import com.hcltech.doctorpatient.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecializationRepository  extends JpaRepository<Specialization, UUID> {

    Specialization getBySpecializationName(String specialist);

    Optional<Specialization> findBySpecializationName(String specializationName);
/*
    Object findBySpecializationNameIgnoreCase(String generalMedicine);*/
}
