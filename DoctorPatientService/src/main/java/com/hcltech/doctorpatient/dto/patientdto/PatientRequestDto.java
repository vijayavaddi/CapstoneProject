package com.hcltech.doctorpatient.dto.patientdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatientRequestDto {
    private Short age;
    private String bloodGroup;
    private String gender;
}
