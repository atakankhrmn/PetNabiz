package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;

import java.util.List;
import java.util.Optional;

public interface ClinicService {

    // Tüm klinikleri getir
    List<Clinic> getAllClinics();

    // ID ile klinik bul
    Optional<Clinic> getClinicById(String clinicId);

    // İsim arama
    List<Clinic> searchClinicsByName(String namePart);

    // Email ile klinik bul
    Optional<Clinic> getClinicByEmail(String email);

    // Klinik oluştur
    Clinic createClinic(Clinic clinic);

    // Klinik güncelle
    Clinic updateClinic(String clinicId, Clinic updatedClinic);

    // Klinik sil
    void deleteClinic(String clinicId);

    // Kliniğe bağlı veterinerleri getir
    List<Veterinary> getVeterinariesByClinic(String clinicId);
}
