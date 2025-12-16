package com.petnabiz.petnabiz.dto.request.admin;
import lombok.Data;

@Data
public class ClinicApplicationDecisionRequestDTO {
    private boolean approve;     // true=approve false=reject
    private String adminNote;    // red sebebi vs
}

