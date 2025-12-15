package com.petnabiz.petnabiz.dto.request.clinic;
import lombok.Data;

@Data
public class ClinicApplicationCreateRequestDTO {
    private String name;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String email;
    private String password; // admin onaylayınca user oluşturmak için
}
