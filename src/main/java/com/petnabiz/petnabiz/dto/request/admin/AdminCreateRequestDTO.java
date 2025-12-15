package com.petnabiz.petnabiz.dto.request.admin;

import lombok.Data;

@Data
public class AdminCreateRequestDTO {

    private String adminId;
    private String email;
    private String password;
    private String name;
}
