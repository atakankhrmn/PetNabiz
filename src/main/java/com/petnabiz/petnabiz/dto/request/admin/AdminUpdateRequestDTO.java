package com.petnabiz.petnabiz.dto.request.admin;

import lombok.Data;

@Data
public class AdminUpdateRequestDTO {

    private String email;
    private String name;
    private Boolean active;
}
