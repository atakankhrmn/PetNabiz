package com.petnabiz.petnabiz.dto.request.auth;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
