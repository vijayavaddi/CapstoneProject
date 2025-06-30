package com.hcltech.doctorpatient.repository;


import com.hcltech.doctorpatient.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    List<Doctor> findBySpecializationId(UUID specializationId);
}
