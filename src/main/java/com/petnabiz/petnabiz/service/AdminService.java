package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    // Tüm admin'leri getir
    List<Admin> getAllAdmins();

    // ID ile admin bul
    Optional<Admin> getAdminById(String adminId);

    // Email ile admin bul (login sonrası vs.)
    Optional<Admin> getAdminByEmail(String email);

    // Full name'e göre arama
    List<Admin> searchAdminsByFullName(String namePart);

    // Yeni admin oluştur
    Admin createAdmin(Admin admin);

    // Var olan admin'i güncelle (şimdilik sadece fullName)
    Admin updateAdmin(String adminId, Admin updatedAdmin);

    // Admin sil
    void deleteAdmin(String adminId);
}
