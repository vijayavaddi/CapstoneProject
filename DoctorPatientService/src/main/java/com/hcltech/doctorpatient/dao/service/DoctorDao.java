package com.hcltech.doctorpatient.dao.service;

import com.hcltech.doctorpatient.model.Doctor;
import com.hcltech.doctorpatient.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorDao {
    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }
    public Doctor create(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor getOneById(UUID doctorId) {
        return doctorRepository.getById(doctorId);
    }

    public void deleteOneById(UUID doctorId) {
        doctorRepository.deleteById(doctorId);
    }
    public Doctor update(Doctor doctor) {
        Optional<Doctor> getDoctorById= doctorRepository.findById(doctor.getDoctorId());

        if (getDoctorById.isPresent()){
            Doctor exsitingDoctor = getDoctorById.get();

            exsitingDoctor.setDoctorId(doctor.getDoctorId());
            exsitingDoctor.setUser(doctor.getUser());
            exsitingDoctor.setQualification(doctor.getQualification());
            exsitingDoctor.setExperience(doctor.getExperience());
            exsitingDoctor.setSpecialization(doctor.getSpecialization());
            //exsitingDoctor.setAppointments(doctor.getAppointments());

            return doctorRepository.save(exsitingDoctor);
        }else {
            return null;
        }
    }
}