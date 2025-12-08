package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Medicine;
import com.petnabiz.petnabiz.repository.MedicineRepository;
import com.petnabiz.petnabiz.service.MedicineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Override
    public Optional<Medicine> getMedicineById(String medicineId) {
        return medicineRepository.findByMedicineId(medicineId);
        // veya: return medicineRepository.findById(medicineId);
    }

    @Override
    public List<Medicine> searchByName(String namePart) {
        return medicineRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    public List<Medicine> getMedicinesByType(String type) {
        return medicineRepository.findByTypeIgnoreCase(type);
    }

    @Override
    public Medicine createMedicine(Medicine medicine) {
        /*
         * Kontroller:
         *  - name boş olmamalı
         *  - type boş olmamalı
         *  - aynı isimde ilaç varsa engellemek isteyebilirsin
         */

        if (medicine.getName() == null || medicine.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name boş olamaz.");
        }

        if (medicine.getType() == null || medicine.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine type boş olamaz.");
        }

        // Aynı isimde bir ilaç zaten var mı? (İstersen kaldırabilirsin)
        Optional<Medicine> existingByName = medicineRepository.findByName(medicine.getName());
        if (existingByName.isPresent()) {
            throw new IllegalStateException("Bu isimde bir medicine zaten kayıtlı: " + medicine.getName());
        }

        // ID çakışması kontrolü (ID'yi sen veriyorsan)
        if (medicine.getMedicineId() != null &&
                medicineRepository.existsByMedicineId(medicine.getMedicineId())) {
            throw new IllegalStateException("Bu ID ile bir medicine zaten kayıtlı: " + medicine.getMedicineId());
        }

        return medicineRepository.save(medicine);
    }

    @Override
    public Medicine updateMedicine(String medicineId, Medicine updatedMedicine) {
        Medicine existing = medicineRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Medicine bulunamadı: " + medicineId));

        // İsim güncelle
        if (updatedMedicine.getName() != null && !updatedMedicine.getName().trim().isEmpty()) {

            // Eğer ismi değiştiriyorsan ve yeni isim başka bir ilaca aitse engelle
            Optional<Medicine> sameName = medicineRepository.findByName(updatedMedicine.getName());
            if (sameName.isPresent() && !sameName.get().getMedicineId().equals(medicineId)) {
                throw new IllegalStateException("Bu isim başka bir medicine için zaten kullanılıyor: "
                        + updatedMedicine.getName());
            }

            existing.setName(updatedMedicine.getName());
        }

        // Type güncelle
        if (updatedMedicine.getType() != null && !updatedMedicine.getType().trim().isEmpty()) {
            existing.setType(updatedMedicine.getType());
        }

        return medicineRepository.save(existing);
    }

    @Override
    public void deleteMedicine(String medicineId) {
        boolean exists = medicineRepository.existsByMedicineId(medicineId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen medicine bulunamadı: " + medicineId);
        }

        medicineRepository.deleteById(medicineId);
    }
}
