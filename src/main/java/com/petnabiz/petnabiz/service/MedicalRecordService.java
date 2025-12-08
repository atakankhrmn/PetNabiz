package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.MedicalRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {

    // Tüm kayıtlar
    List<MedicalRecord> getAllRecords();

    // ID ile kayıt
    Optional<MedicalRecord> getRecordById(String recordId);

    // Pet bazlı kayıtlar
    List<MedicalRecord> getRecordsByPetId(String petId);

    // Vet bazlı kayıtlar
    List<MedicalRecord> getRecordsByVeterinaryId(String vetId);

    // Tarih bazlı
    List<MedicalRecord> getRecordsByDate(LocalDate date);

    List<MedicalRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    // Yeni kayıt oluştur
    MedicalRecord createRecord(MedicalRecord record);

    // Kayıt güncelle
    MedicalRecord updateRecord(String recordId, MedicalRecord updatedRecord);

    // Kayıt sil
    void deleteRecord(String recordId);
}
