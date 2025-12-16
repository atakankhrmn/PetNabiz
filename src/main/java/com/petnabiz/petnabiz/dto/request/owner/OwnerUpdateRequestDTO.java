package com.petnabiz.petnabiz.dto.request.owner;

import lombok.Data;

@Data
public class OwnerUpdateRequestDTO {
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private Boolean active;
}
