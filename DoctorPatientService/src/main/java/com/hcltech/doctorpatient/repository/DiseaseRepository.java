package com.hcltech.doctorpatient.repository;

import com.hcltech.doctorpatient.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, UUID> {
    Disease findByName(String disease_name);
}