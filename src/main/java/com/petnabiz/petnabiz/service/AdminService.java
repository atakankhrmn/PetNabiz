package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.dto.request.admin.AdminCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.admin.AdminUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.admin.AdminResponseDTO;

import java.util.List;

public interface AdminService {

    // Tüm admin'leri getir
    List<AdminResponseDTO> getAllAdmins();

    // ID ile admin getir
    AdminResponseDTO getAdminById(String adminId);

    // Email ile admin getir (login / admin paneli)
    AdminResponseDTO getAdminByEmail(String email);

    // İsme göre arama
    List<AdminResponseDTO> searchAdminsByFullName(String namePart);

    // Yeni admin oluştur
    AdminResponseDTO createAdmin(AdminCreateRequestDTO dto);

    // Admin güncelle
    AdminResponseDTO updateAdmin(String adminId, AdminUpdateRequestDTO dto);

    // Admin sil
    void deleteAdmin(String adminId);
}
