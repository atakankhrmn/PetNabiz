package com.petnabiz.petnabiz.dto.request.user;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private String email;
    private Boolean active; // null ise dokunma
    private String role;
}
