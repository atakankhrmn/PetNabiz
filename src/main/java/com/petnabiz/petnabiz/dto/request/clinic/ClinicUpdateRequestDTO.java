package com.petnabiz.petnabiz.dto.request.clinic;

import lombok.Data;

@Data
public class ClinicUpdateRequestDTO {
    private String email;     // user.email
    private Boolean active;   // user.active

    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;
}
