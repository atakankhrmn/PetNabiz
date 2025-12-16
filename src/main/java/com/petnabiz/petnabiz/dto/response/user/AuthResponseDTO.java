package com.petnabiz.petnabiz.dto.response.user;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private boolean authenticated;
    private UserResponseDTO user; // başarılıysa dolu
}
