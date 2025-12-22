package com.petnabiz.petnabiz.dto.response.auth;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String userId;
    private String role;
    private boolean active;
}
