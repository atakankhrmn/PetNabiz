package com.petnabiz.petnabiz.dto.response.admin;
import lombok.Data;

@Data
public class ClinicApplicationDecisionResponseDTO {
    private String applicationId;
    private String status;
    private String adminNote;
    private String createdClinicId; // approve ise dolu, reject ise null
}

