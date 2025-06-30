package com.hcltech.doctorpatient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @Column(name="patient_id")
    private UUID patientId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "patient_id")
    private User user;
    private Short age;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    public enum Gender {
        MALE,
        FEMALE,
        OTHERS
    }

}
