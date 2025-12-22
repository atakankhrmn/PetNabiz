package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.admin.AdminCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.admin.AdminUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.admin.AdminResponseDTO;
import com.petnabiz.petnabiz.mapper.AdminMapper;
import com.petnabiz.petnabiz.model.Admin;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.User;
import com.petnabiz.petnabiz.repository.AdminRepository;
import com.petnabiz.petnabiz.repository.UserRepository;
import com.petnabiz.petnabiz.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminRepository adminRepository,
                            UserRepository userRepository,
                            AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.adminMapper = adminMapper;
    }

    @Override
    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    @Override
    public AdminResponseDTO getAdminById(String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin bulunamadı: " + adminId));
        return adminMapper.toResponse(admin);
    }

    @Override
    public AdminResponseDTO getAdminByEmail(String email) {
        Admin admin = adminRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Admin bulunamadı (email): " + email));
        return adminMapper.toResponse(admin);
    }

    @Override
    public List<AdminResponseDTO> searchAdminsByFullName(String namePart) {
        return adminRepository.findByFullNameContainingIgnoreCase(namePart)
                .stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    /**
     * DTO ile admin oluşturma:
     * - users tablosuna ROLE_ADMIN user yaratılır
     * - MapsId: adminId = userId
     * - admins tablosuna Admin yaratılır
     */

    @Override
    @Transactional
    public AdminResponseDTO createAdmin(AdminCreateRequestDTO dto) {

        // ... validasyonlar ve email kontrolü ...

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        // ✅ OTOMATİK ATAMA BURADA YAPILIYOR
        user.setActive(true);       // 1 olarak kaydolur
        user.setRole("ROLE_ADMIN"); // Rolü kod içinde biz belirliyoruz

        // ... kaydetme işlemleri ...

        User savedUser = userRepository.save(user);

        Admin admin = new Admin();
        admin.setUser(savedUser); // @MapsId ile ID eşitlenir
        admin.setFullName(dto.getName());

        adminRepository.save(admin); // Kaydet

        return adminMapper.toResponse(admin);
    }

    @Override
    @Transactional
    public AdminResponseDTO updateAdmin(String adminId, AdminUpdateRequestDTO dto) {
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin bulunamadı: " + adminId));

        if (dto.getName() != null) {
            admin.setFullName(dto.getName());
        }

        // Email ve active User tarafında
        if (admin.getUser() == null) {
            throw new IllegalStateException("Admin'in user kaydı yok: " + adminId);
        }

        if (dto.getEmail() != null) {
            // Email değişiyorsa çakışma kontrolü
            userRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                if (!existing.getUserId().equals(admin.getUser().getUserId())) {
                    throw new IllegalArgumentException("Bu email zaten kullanılıyor: " + dto.getEmail());
                }
            });
            admin.getUser().setEmail(dto.getEmail());
        }

        if (dto.getActive() != null) {
            admin.getUser().setActive(dto.getActive());
        }

        // save admin; user cascade yoksa userRepository.save(...) da gerekebilir
        Admin saved = adminRepository.save(admin);

        return adminMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAdmin(String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Silinecek admin bulunamadı: " + adminId));

        // İstersen user'ı da sil: (FK durumuna göre)
        // userRepository.deleteById(admin.getUser().getUserId());

        adminRepository.deleteById(adminId);
    }
}
