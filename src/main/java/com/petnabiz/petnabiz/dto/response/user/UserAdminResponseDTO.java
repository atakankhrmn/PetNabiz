package com.petnabiz.petnabiz.dto.response.user;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserAdminResponseDTO {
    private String userId;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}

