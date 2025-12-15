package com.petnabiz.petnabiz.dto.request.vet;

import lombok.Data;

@Data
public class VetCreateRequestDTO {
    private String vetId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String certificate;
    private String clinicId;
    private String email;
    private String password;
}
