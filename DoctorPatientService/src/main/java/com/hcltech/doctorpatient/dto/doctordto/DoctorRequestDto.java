package com.hcltech.doctorpatient.dto.doctordto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequestDto {
    private String experience;
    private String qualification;
    private String specialist;
}
