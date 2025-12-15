package com.petnabiz.petnabiz.dto.response.vet;

import lombok.Data;
import com.petnabiz.petnabiz.dto.summary.ClinicSummaryDTO;

@Data
public class VetResponseDTO {
    private String vetId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String certificate;
    private boolean active;
    private ClinicSummaryDTO clinic;
}
