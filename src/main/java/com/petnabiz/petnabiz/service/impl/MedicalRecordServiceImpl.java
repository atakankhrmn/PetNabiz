package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.MedicalRecordRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.MedicalRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    PetRepository petRepository,
                                    VeterinaryRepository veterinaryRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
        this.veterinaryRepository = veterinaryRepository;
    }

    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    @Override
    public Optional<MedicalRecord> getMedicalRecordById(String recordId) {
        return medicalRecordRepository.findByRecordId(recordId);
        // veya: return medicalRecordRepository.findById(recordId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByPetId(String petId) {
        return medicalRecordRepository.findByPet_PetId(petId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByVeterinaryId(String vetId) {
        return medicalRecordRepository.findByVeterinary_VetId(vetId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByDate(LocalDate date) {
        return medicalRecordRepository.findByDate(date);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return medicalRecordRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public MedicalRecord createMedicalRecord(MedicalRecord record) {
        /*
         * Beklenen:
         *  - record.getPet().getPetId() dolu
         *  - record.getVeterinary().getVetId() dolu
         *  - record.getDate() dolu
         */

        if (record.getPet() == null || record.getPet().getPetId() == null) {
            throw new IllegalArgumentException("MedicalRecord için pet bilgisi zorunlu.");
        }
        if (record.getVeterinary() == null || record.getVeterinary().getVetId() == null) {
            throw new IllegalArgumentException("MedicalRecord için veterinary bilgisi zorunlu.");
        }
        if (record.getDate() == null) {
            throw new IllegalArgumentException("MedicalRecord için date zorunlu.");
        }

        String petId = record.getPet().getPetId();
        String vetId = record.getVeterinary().getVetId();

        Pet pet = petRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + petId));

        Veterinary vet = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + vetId));

        // Managed entity'ler set ediliyor
        record.setPet(pet);
        record.setVeterinary(vet);

        return medicalRecordRepository.save(record);
    }

    @Override
    public MedicalRecord updateMedicalRecord(String recordId, MedicalRecord updatedRecord) {
        MedicalRecord existing = medicalRecordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord bulunamadı: " + recordId));

        // Tarih
        if (updatedRecord.getDate() != null) {
            existing.setDate(updatedRecord.getDate());
        }

        // Açıklama
        if (updatedRecord.getDescription() != null) {
            existing.setDescription(updatedRecord.getDescription());
        }

        // Pet değişimi
        if (updatedRecord.getPet() != null && updatedRecord.getPet().getPetId() != null) {
            String newPetId = updatedRecord.getPet().getPetId();
            Pet newPet = petRepository.findByPetId(newPetId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni pet bulunamadı: " + newPetId));
            existing.setPet(newPet);
        }

        // Vet değişimi
        if (updatedRecord.getVeterinary() != null &&
                updatedRecord.getVeterinary().getVetId() != null) {

            String newVetId = updatedRecord.getVeterinary().getVetId();
            Veterinary newVet = veterinaryRepository.findByVetId(newVetId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni veterinary bulunamadı: " + newVetId));
            existing.setVeterinary(newVet);
        }

        return medicalRecordRepository.save(existing);
    }

    @Override
    public void deleteMedicalRecord(String recordId) {
        boolean exists = medicalRecordRepository.existsById(recordId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen MedicalRecord bulunamadı: " + recordId);
        }

        medicalRecordRepository.deleteById(recordId);
    }
}
