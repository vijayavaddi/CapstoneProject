package com.hcltech.doctorpatient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationDto {

    UUID id;
    String specialization;
}
