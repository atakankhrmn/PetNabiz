package com.petnabiz.petnabiz.dto.response.petowner;

import lombok.Data;
import java.util.List;

@Data
public class PetOwnerResponseDTO {
    private String ownerId;

    private String email;     // user.email
    private boolean active;   // user.active

    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    private List<String> petIds; // full pet listesi basmayalım
}
