package com.petnabiz.petnabiz.dto.response.clinicapplication;

import com.petnabiz.petnabiz.model.ApplicationStatus;
import lombok.Data;

@Data
public class ClinicApplicationResponseDTO {

    private Long id;

    private String clinicName;
    private String email;
    private String phone;
    private String city;
    private String district;
    private String address;

    private ApplicationStatus status;
}