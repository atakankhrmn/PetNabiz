package com.petnabiz.petnabiz.dto.request.veterinary;

import lombok.Data;

@Data
public class VeterinaryUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String certificate;
    private String clinicId; // değişecekse
}
