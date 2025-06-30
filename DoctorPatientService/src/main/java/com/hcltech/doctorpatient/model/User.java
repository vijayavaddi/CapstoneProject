package com.hcltech.doctorpatient.model;


import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name= "users")
@Getter
@Setter
@NoArgsConstructor
@Data

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 1000)
    private String lastName;

    @Column(unique = true)
    private String mobile;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctor;
    public static Snippet builder() {
        return null;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
