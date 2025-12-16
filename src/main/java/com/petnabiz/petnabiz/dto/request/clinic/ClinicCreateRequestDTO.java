package com.petnabiz.petnabiz.dto.request.clinic;

import lombok.Data;

@Data
public class ClinicCreateRequestDTO {
    private String clinicId;   // = userId (MapsId)
    private String email;
    private String password;   // user password
    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;
}
