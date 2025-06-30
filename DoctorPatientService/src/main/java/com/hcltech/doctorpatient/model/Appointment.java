package com.hcltech.doctorpatient.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="appontments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_time", nullable = false)
    private LocalDateTime fromTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "to_time", nullable = false)
    private LocalDateTime toTime;

    @Column(name="description",nullable = false)
    private String description;

    public enum Status {
        COMPLETED,
        CANCELLED,
        SCHEDULED,
        RESCHEDULED
    }


}





