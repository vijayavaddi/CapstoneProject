package com.hcltech.doctorpatient.dto.appointmentdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentResponseDto {
    private String patientName;
    private String doctorName;
    private String diseaseName;
    private String status;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
}
