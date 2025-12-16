package com.petnabiz.petnabiz.dto.request.auth;
import lombok.Data;

@Data
public class RegisterClinicRequestDTO {
    private String clinicId;
    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String email;
    private String password;
}

