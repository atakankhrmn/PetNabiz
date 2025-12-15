package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.dto.request.admin.AdminCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.admin.AdminUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.admin.AdminResponseDTO;
import com.petnabiz.petnabiz.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Tüm adminleri getir
     */
    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    /**
     * ID'ye göre admin getir
     */
    @GetMapping("/{adminId}")
    public ResponseEntity<AdminResponseDTO> getAdminById(
            @PathVariable String adminId
    ) {
        return ResponseEntity.ok(adminService.getAdminById(adminId));
    }

    /**
     * Yeni admin oluştur
     */
    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(
            @RequestBody AdminCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(adminService.createAdmin(dto));
    }

    /**
     * Admin güncelle
     */
    @PutMapping("/{adminId}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable String adminId,
            @RequestBody AdminUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(adminService.updateAdmin(adminId, dto));
    }

    /**
     * Admin sil
     */
    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(
            @PathVariable String adminId
    ) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}
