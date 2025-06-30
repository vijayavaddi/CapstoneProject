package com.hcltech.doctorpatient.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id"})
})
@Data
public class Doctor {

    @Id
    @Column(name = "doctor_id", nullable = false, unique = true)
    private UUID doctorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false,referencedColumnName = "user_id")
    private User user;

    @Column(name = "experience", nullable = false, length = 100)
    private String experience;

    @Column(name = "qualification", nullable = false, length = 100)
    private String qualification;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", referencedColumnName = "specialization_id",nullable = false)
    private Specialization specialization;

}
