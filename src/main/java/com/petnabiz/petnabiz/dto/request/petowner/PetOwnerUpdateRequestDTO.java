package com.petnabiz.petnabiz.dto.request.petowner;

import lombok.Data;

@Data
public class PetOwnerUpdateRequestDTO {
    private String email;      // user.email
    private Boolean active;    // user.active

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
