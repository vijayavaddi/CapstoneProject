package com.hcltech.doctorpatient.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DiseaseDTO {
    private UUID diseaseId;
    private String diseaseName;
    private String specializatonName;
}