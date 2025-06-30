package com.hcltech.doctorpatient.dto.doctordto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDto {
    private UUID doctorId;
    private String name;
    private String mobileNumber;
    private String experience;
    private String specialist;
}

