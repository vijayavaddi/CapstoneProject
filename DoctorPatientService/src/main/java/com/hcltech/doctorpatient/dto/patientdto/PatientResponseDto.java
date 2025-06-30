package com.hcltech.doctorpatient.dto.patientdto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {
    private UUID id;
    private String name;
    private String mobileNumber;
    private Short age;
    private String bloodGroup;
    private String gender;


}