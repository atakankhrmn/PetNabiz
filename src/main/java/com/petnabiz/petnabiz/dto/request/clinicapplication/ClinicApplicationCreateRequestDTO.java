package com.petnabiz.petnabiz.dto.request.clinicapplication;

import lombok.Data;

@Data
public class ClinicApplicationCreateRequestDTO {

    private String email;
    private String password;   // düz şifre (service'te hashlenir)

    private String clinicName;
    private String city;
    private String district;
    private String address;
    private String phone;
}