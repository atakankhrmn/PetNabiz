package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.auth.RegisterOwnerRequestDTO;
import com.petnabiz.petnabiz.dto.response.auth.AuthOwnerResponseDTO;

public interface AuthService {
    AuthOwnerResponseDTO registerOwner(RegisterOwnerRequestDTO dto);
    AuthOwnerResponseDTO me(String email);
}
