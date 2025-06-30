package com.hcltech.doctorpatient.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.action.internal.OrphanRemovalAction;

import java.util.UUID;

@Entity
@Table(name = "Diseases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID diseaseId;

    @Column(name="disease_name",nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

}