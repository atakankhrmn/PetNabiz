package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.veterinary.VeterinaryUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.veterinary.VeterinaryResponseDTO;
import com.petnabiz.petnabiz.mapper.VeterinaryMapper;
import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.ClinicRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.VeterinaryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

import java.util.List;

@Service("veterinaryService") // SpEL için
public class VeterinaryServiceImpl implements VeterinaryService {

    private final VeterinaryRepository veterinaryRepository;
    private final ClinicRepository clinicRepository;
    private final VeterinaryMapper veterinaryMapper;

    public VeterinaryServiceImpl(VeterinaryRepository veterinaryRepository,
                                 ClinicRepository clinicRepository,
                                 VeterinaryMapper veterinaryMapper) {
        this.veterinaryRepository = veterinaryRepository;
        this.clinicRepository = clinicRepository;
        this.veterinaryMapper = veterinaryMapper;
    }

    // ---- helpers ----
    @Override
    public boolean isClinicOwner(String clinicEmail, String clinicId) {
        if (clinicEmail == null || clinicEmail.isBlank() || clinicId == null || clinicId.isBlank()) return false;

        Clinic clinic = clinicRepository.findByClinicId(clinicId).orElse(null);
        if (clinic == null || clinic.getUser() == null || clinic.getUser().getEmail() == null) return false;

        return clinicEmail.equalsIgnoreCase(clinic.getUser().getEmail());
    }

    @Override
    public boolean isClinicOwnerOfVet(String clinicEmail, String vetId) {
        if (clinicEmail == null || clinicEmail.isBlank() || vetId == null || vetId.isBlank()) return false;

        Veterinary vet = veterinaryRepository.findByVetId(vetId).orElse(null);
        if (vet == null || vet.getClinic() == null || vet.getClinic().getUser() == null) return false;

        String email = vet.getClinic().getUser().getEmail();
        return email != null && clinicEmail.equalsIgnoreCase(email);
    }

    // ---- reads ----
    @Override
    public List<VeterinaryResponseDTO> getAllVeterinaries() {
        return veterinaryRepository.findAll().stream()
                .map(veterinaryMapper::toResponse)
                .toList();
    }

    @Override
    public VeterinaryResponseDTO getVeterinaryById(String vetId) {
        Veterinary v = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Veterinary bulunamadı: " + vetId));
        return veterinaryMapper.toResponse(v);
    }

    @Override
    public List<VeterinaryResponseDTO> getVeterinariesByClinicId(String clinicId) {
        clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + clinicId));

        return veterinaryRepository.findByClinic_ClinicId(clinicId).stream()
                .map(veterinaryMapper::toResponse)
                .toList();
    }

    // ---- writes ----
    @Override
    @Transactional
    public VeterinaryResponseDTO createVeterinary(VeterinaryCreateRequestDTO dto, MultipartFile file) { // Parametreye file eklendi

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Lütfen veteriner hekim diplomasını yükleyiniz.");
        }

        // 1. Validasyonlar
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary firstName boş olamaz.");
        }
        // ... diğer validasyonlar ...

        // 2. Entity Oluşturma
        Veterinary v = new Veterinary();

        // A) UUID Oluşturma (Benzersiz ID)
        v.setVetId(UUID.randomUUID().toString());

        v.setFirstName(dto.getFirstName());
        v.setLastName(dto.getLastName());
        v.setPhoneNumber(dto.getPhoneNumber());
        v.setAddress(dto.getAddress());

        // 3. Dosya Yükleme İşlemi (Varsa)
        if (file != null && !file.isEmpty()) {
            try {
                // Dosyayı kaydet ve yolunu al
                String filePath = saveFile(file, v.getVetId());
                v.setCertificate(filePath); // Veritabanına dosyanın yolunu/adını kaydediyoruz
            } catch (IOException e) {
                throw new RuntimeException("Dosya yüklenirken hata oluştu: " + e.getMessage());
            }
        } else {
            // Dosya yoksa DTO'dan gelen manuel metni koyabilirsin veya boş bırakırsın
            v.setCertificate(dto.getCertificate());
        }

        // 4. Clinic Bağlama
        if (dto.getClinicId() != null && !dto.getClinicId().trim().isEmpty()) {
            Clinic clinic = clinicRepository.findByClinicId(dto.getClinicId())
                    .orElseThrow(() -> new EntityNotFoundException("Clinic bulunamadı: " + dto.getClinicId()));
            v.setClinic(clinic);
        }

        Veterinary saved = veterinaryRepository.save(v);
        return veterinaryMapper.toResponse(saved);
    }

    // --- YARDIMCI METOD (Dosyayı diske kaydeder) ---
    private String saveFile(MultipartFile file, String vetId) throws IOException {
        // Proje kök dizininde 'uploads' klasörü olsun
        String uploadDir = "uploads/certificates/";

        // Klasör yoksa oluştur
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Dosya adı çakışmasın diye vetId + orijinal isim yapalım
        // Örnek: "123e4567-e89b..._diploma.pdf"
        String fileName = vetId + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Dosyayı kopyala
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Geriye dosya adını veya tam yolu döndür
        // Frontend'de bu dosyayı sunmak için statik resource ayarı gerekebilir
        return fileName;
    }
    @Override
    @Transactional
    public VeterinaryResponseDTO updateVeterinary(String vetId, VeterinaryUpdateRequestDTO dto) {

        Veterinary existing = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Veterinary bulunamadı: " + vetId));

        if (dto.getFirstName() != null && !dto.getFirstName().trim().isEmpty()) {
            existing.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().trim().isEmpty()) {
            existing.setLastName(dto.getLastName());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            existing.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddress() != null) {
            existing.setAddress(dto.getAddress());
        }
        if (dto.getCertificate() != null) {
            existing.setCertificate(dto.getCertificate());
        }

        // Klinik değiştirme CLINIC için tehlikeli -> bence sadece ADMIN yapmalı.
        // Yine de bırakacaksan:
        if (dto.getClinicId() != null && !dto.getClinicId().trim().isEmpty()) {
            Clinic newClinic = clinicRepository.findByClinicId(dto.getClinicId())
                    .orElseThrow(() -> new EntityNotFoundException("Yeni clinic bulunamadı: " + dto.getClinicId()));
            existing.setClinic(newClinic);
        }

        Veterinary saved = veterinaryRepository.save(existing);
        return veterinaryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteVeterinary(String vetId) {
        Veterinary v = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen veterinary bulunamadı: " + vetId));
        veterinaryRepository.delete(v);
    }

    @Override
    public List<VeterinaryResponseDTO> getAllMyVeterinaries() {

        String email = currentEmail();

        Clinic clinic = clinicRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Clinic not found for email: " + email));

        return veterinaryRepository.findByClinic_ClinicId(clinic.getClinicId())
                .stream()
                .map(veterinaryMapper::toResponse)
                .toList();
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("Authentication yok.");
        return auth.getName(); // username = email (SecurityUserDetailsService)
    }

    @Override
    public Resource getCertificateResource(String vetId) {
        Veterinary vet = veterinaryRepository.findById(vetId)
                .orElseThrow(() -> new EntityNotFoundException("Veteriner bulunamadı"));

        String fileName = vet.getCertificate();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Bu veteriner için sertifika yüklenmemiş.");
        }

        try {
            // Kaydederken kullandığımız yol: uploads/certificates/
            Path filePath = Paths.get("uploads/certificates/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Dosya bulunamadı veya okunamıyor: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Dosya yolu hatası: " + e.getMessage());
        }
    }

}
