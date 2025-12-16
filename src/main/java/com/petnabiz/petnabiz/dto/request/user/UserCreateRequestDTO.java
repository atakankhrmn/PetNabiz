package com.petnabiz.petnabiz.dto.request.user;

import lombok.Data;

@Data
public class UserCreateRequestDTO {
    private String userId;
    private String email;
    private String password;
    private boolean active = true;
    private String role;
}
