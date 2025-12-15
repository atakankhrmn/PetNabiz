package com.petnabiz.petnabiz.dto.response.user;
import lombok.Data;

@Data
public class UserResponseDTO {
    private String userId;
    private String email;
    private String role;
    private boolean active;
}
