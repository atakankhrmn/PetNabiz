package com.petnabiz.petnabiz.dto.request.petowner;

import lombok.Data;

@Data
public class PetOwnerCreateRequestDTO {
    private String ownerId;    // = userId (MapsId)
    private String email;      // user.email
    private String password;   // user.password (şimdilik plain, sonra hash)
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
