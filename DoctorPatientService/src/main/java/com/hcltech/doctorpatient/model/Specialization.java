package com.hcltech.doctorpatient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "specializations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"specialization_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialization {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy= "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", name = "specialization_id", nullable = false, updatable = false)
    private UUID id;



    @Column(name = "specialization_name", nullable = false, length = 25, unique = true)
    private String specializationName;

    @OneToMany(mappedBy = "specialization",cascade = CascadeType.ALL)
    private List<Disease> diseases;


}
