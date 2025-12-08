package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicineService {

    // Tüm ilaçları listele
    List<Medicine> getAllMedicines();

    // ID ile bul
    Optional<Medicine> getMedicineById(String medicineId);

    // İsim ile arama
    List<Medicine> searchByName(String namePart);

    // Type ile filtre (tablet, syrup vb.)
    List<Medicine> getMedicinesByType(String type);

    // Yeni medicine oluştur
    Medicine createMedicine(Medicine medicine);

    // Medicine güncelle
    Medicine updateMedicine(String medicineId, Medicine updatedMedicine);

    // Medicine sil
    void deleteMedicine(String medicineId);
}
