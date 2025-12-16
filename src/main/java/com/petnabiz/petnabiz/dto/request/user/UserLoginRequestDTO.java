package com.petnabiz.petnabiz.dto.request.user;
import lombok.Data;

@Data
public class UserLoginRequestDTO {
    private String email;
    private String password;
}

