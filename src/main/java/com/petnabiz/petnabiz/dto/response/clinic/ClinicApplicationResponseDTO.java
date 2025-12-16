package com.petnabiz.petnabiz.dto.response.clinic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClinicApplicationResponseDTO {
    private String applicationId;
    private String name;
    private String city;
    private String district;
    private String phone;
    private String email;
    private String status; // PENDING / APPROVED / REJECTED
    private String adminNote; // optional
    private LocalDateTime createdAt;
}

