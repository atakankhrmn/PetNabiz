package com.petnabiz.petnabiz.dto.response.veterinary;

import lombok.Data;

@Data
public class VeterinaryResponseDTO {
    private String vetId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String certificate;

    private String clinicId;   // sadece id
    private String clinicName; // opsiyonel, güzel olur
}
