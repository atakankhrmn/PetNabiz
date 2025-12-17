package com.petnabiz.petnabiz.dto.response.auth;

import lombok.Data;

@Data
public class AuthOwnerResponseDTO {
    private String userId;
    private String ownerId;

    private String email;
    private String role;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
