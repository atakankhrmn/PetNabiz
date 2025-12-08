package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Veterinary;

import java.util.List;
import java.util.Optional;

public interface VeterinaryService {

    // Tüm vet'leri listele
    List<Veterinary> getAllVeterinaries();

    // ID ile vet bul
    Optional<Veterinary> getVeterinaryById(String vetId);

    // İsim aramaları-KULLANIRSA CLINIC KULLANIR
    List<Veterinary> searchByFirstName(String firstNamePart);

    List<Veterinary> searchByLastName(String lastNamePart);

    // Telefon numarası ile bul
    Optional<Veterinary> getByPhoneNumber(String phoneNumber);

    // Sertifika / diploma bilgisine göre arama
    List<Veterinary> searchByCertificate(String certificatePart);

    // Belirli bir kliniğe bağlı vet'ler
    List<Veterinary> getVeterinariesByClinicId(String clinicId);

    // Yeni vet oluştur
    Veterinary createVeterinary(Veterinary veterinary);

    // Var olan vet'i güncelle
    Veterinary updateVeterinary(String vetId, Veterinary updatedVeterinary);

    // Vet sil
    void deleteVeterinary(String vetId);
}
