package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.auth.RegisterOwnerRequestDTO;
import com.petnabiz.petnabiz.dto.response.auth.AuthOwnerResponseDTO;
import com.petnabiz.petnabiz.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // PUBLIC: Owner register
    @PostMapping("/register/owner")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthOwnerResponseDTO> registerOwner(@Valid @RequestBody RegisterOwnerRequestDTO dto) {
        AuthOwnerResponseDTO created = authService.registerOwner(dto);
        return ResponseEntity
                .created(URI.create("/api/auth/me"))
                .body(created);
    }

    // LOGIN TEST / ME endpoint:
    // Basic Auth ile giriş yapınca authentication.getName() = email olur
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER','CLINIC')")
    public ResponseEntity<AuthOwnerResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }
}
