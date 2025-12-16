package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.admin.AdminCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.admin.AdminUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.admin.AdminResponseDTO;
import com.petnabiz.petnabiz.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('ADMIN')") // BU controller'a gelen herkes ADMIN olmalı
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable String adminId) {
        return ResponseEntity.ok(adminService.getAdminById(adminId));
    }

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminCreateRequestDTO dto) {
        AdminResponseDTO created = adminService.createAdmin(dto);

        // DTO'da id alanın adı farklıysa burayı düzelt (getAdminId/getId vs.)
        URI location = URI.create("/api/admins/" + created.getAdminId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable String adminId,
            @Valid @RequestBody AdminUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(adminService.updateAdmin(adminId, dto));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}
