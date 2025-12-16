package com.petnabiz.petnabiz.dto.request.auth;
import lombok.Data;

@Data
public class RegisterOwnerRequestDTO {
    private String ownerId;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private String password;
}

