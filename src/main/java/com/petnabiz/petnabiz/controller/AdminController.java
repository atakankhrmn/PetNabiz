package com.petnabiz.petnabiz.controller;

import com.petnabiz.petnabiz.model.Admin;
import com.petnabiz.petnabiz.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    // Constructor injection
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Tüm adminleri getir
     * GET /api/admins
     */
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    /**
     * ID'ye göre admin getir
     * GET /api/admins/{adminId}
     */
    @GetMapping("/{adminId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable String adminId) {
        Optional<Admin> adminOpt = adminService.getAdminById(adminId);
        return adminOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Yeni admin oluştur
     * POST /api/admins
     *
     * Body örneği:
     * {
     *   "adminId": "A001",
     *   "email": "admin@petnabiz.com",
     *   "password": "123456",
     *   "name": "System Admin"
     * }
     */
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin created = adminService.createAdmin(admin);
        return ResponseEntity.ok(created);
    }

    /**
     * Admin güncelle
     * PUT /api/admins/{adminId}
     */
    @PutMapping("/{adminId}")
    public ResponseEntity<Admin> updateAdmin(
            @PathVariable String adminId,
            @RequestBody Admin updatedAdmin
    ) {
        Admin updated = adminService.updateAdmin(adminId, updatedAdmin);
        return ResponseEntity.ok(updated);
    }

    /**
     * Admin sil
     * DELETE /api/admins/{adminId}
     */
    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}
