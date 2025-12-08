package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Admin;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.AdminRepository;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AdminServiceImpl(AdminRepository adminRepository,
                            UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Optional<Admin> getAdminById(String adminId) {
        return adminRepository.findByAdminId(adminId);
        // veya: return adminRepository.findById(adminId);
    }

    @Override
    public Optional<Admin> getAdminByEmail(String email) {
        // Admin.user.email üzerinden
        return adminRepository.findByUserEmail(email);
    }

    @Override
    public List<Admin> searchAdminsByFullName(String namePart) {
        return adminRepository.findByFullNameContainingIgnoreCase(namePart);
    }

    @Override
    public Admin createAdmin(Admin admin) {
        /*
         * Tasarım: adminId = userId (MapsId)
         * Yani önce User'ı bulmamız, sonra Admin'e set etmemiz gerekiyor.
         */

        if (admin.getUser() == null) {
            throw new IllegalArgumentException("Admin için user bilgisi zorunlu.");
        }

        User adminUser;

        // Öncelik: userId ile arama
        if (admin.getUser().getUserId() != null) {
            String userId = admin.getUser().getUserId();
            adminUser = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + userId));
        } else if (admin.getUser().getEmail() != null) {
            // userId yoksa email ile dene
            String email = admin.getUser().getEmail();
            adminUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User bulunamadı: " + email));
        } else {
            throw new IllegalArgumentException("User için userId veya email sağlanmalı.");
        }

        // MapsId gereği: adminId = userId
        admin.setAdminId(adminUser.getUserId());
        admin.setUser(adminUser);

        return adminRepository.save(admin);
    }

    @Override
    public Admin updateAdmin(String adminId, Admin updatedAdmin) {
        Admin existingAdmin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin bulunamadı: " + adminId));

        // Şu an Admin'de sadece fullName alanı var (user login bilgilerine dokunmuyoruz)
        existingAdmin.setFullName(updatedAdmin.getFullName());

        return adminRepository.save(existingAdmin);
    }

    @Override
    public void deleteAdmin(String adminId) {
        boolean exists = adminRepository.existsByAdminId(adminId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen admin bulunamadı: " + adminId);
        }

        adminRepository.deleteById(adminId);
    }
}
