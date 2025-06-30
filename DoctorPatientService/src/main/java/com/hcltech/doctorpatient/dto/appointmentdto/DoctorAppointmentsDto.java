package com.hcltech.doctorpatient.dto.appointmentdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DoctorAppointmentsDto {
    private String patient_name;
    private String disease_name;
    private String status;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
}