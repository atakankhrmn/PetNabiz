package com.petnabiz.petnabiz.dto.request.clinic;

import lombok.Data;

@Data
public class ClinicUpdateRequestDTO {
    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String email;
    private Boolean active;
}
