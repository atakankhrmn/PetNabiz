package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.MedicalRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {

    // Tüm kayıtlar
    List<MedicalRecord> getAllMedicalRecords();

    // ID ile kayıt
    Optional<MedicalRecord> getMedicalRecordById(String recordId);

    // Pet bazlı kayıtlar
    List<MedicalRecord> getMedicalRecordsByPetId(String petId);

    // Vet bazlı kayıtlar
    List<MedicalRecord> getMedicalRecordsByVeterinaryId(String vetId);

    // Tarih bazlı
    List<MedicalRecord> getMedicalRecordsByDate(LocalDate date);

    List<MedicalRecord> getMedicalRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    // Yeni kayıt oluştur
    MedicalRecord createMedicalRecord(MedicalRecord record);

    // Kayıt güncelle
    MedicalRecord updateMedicalRecord(String recordId, MedicalRecord updatedRecord);

    // Kayıt sil
    void deleteMedicalRecord(String recordId);
}
