package com.petnabiz.petnabiz.dto.response.admin;

import lombok.Data;

@Data
public class AdminResponseDTO {

    private String adminId;
    private String email;
    private String name;
    private boolean active;
}
