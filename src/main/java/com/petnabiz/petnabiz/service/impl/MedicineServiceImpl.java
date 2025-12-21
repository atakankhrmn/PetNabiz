package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.medicine.MedicineCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicine.MedicineUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicine.MedicineResponseDTO;
import com.petnabiz.petnabiz.mapper.MedicineMapper;
import com.petnabiz.petnabiz.model.Medicine;
import com.petnabiz.petnabiz.repository.MedicineRepository;
import com.petnabiz.petnabiz.service.MedicineService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    public MedicineServiceImpl(MedicineRepository medicineRepository,
                               MedicineMapper medicineMapper) {
        this.medicineRepository = medicineRepository;
        this.medicineMapper = medicineMapper;
    }

    @Override
    public List<MedicineResponseDTO> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(medicineMapper::toResponse)
                .toList();
    }

    @Override
    public MedicineResponseDTO getMedicineById(String medicineId) {
        Medicine m = medicineRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("Medicine bulunamadı: " + medicineId));
        return medicineMapper.toResponse(m);
    }

    @Override
    public List<MedicineResponseDTO> searchByName(String namePart) {
        return medicineRepository.findByNameContainingIgnoreCase(namePart)
                .stream()
                .map(medicineMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicineResponseDTO> getMedicinesByType(String type) {
        return medicineRepository.findByTypeIgnoreCase(type)
                .stream()
                .map(medicineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MedicineResponseDTO createMedicine(MedicineCreateRequestDTO dto) {

        // 1. Validasyonlar
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name boş olamaz.");
        }
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine type boş olamaz.");
        }

        // 2. Aynı isimde ilaç var mı kontrolü
        Optional<Medicine> existingByName = medicineRepository.findByName(dto.getName());
        if (existingByName.isPresent()) {
            throw new IllegalStateException("Bu isimde bir medicine zaten kayıtlı: " + dto.getName());
        }

        Medicine m = new Medicine();

        // --- EKLENEN KISIM: ID ÜRETİMİ ---
        // Veritabanına kaydetmeden önce benzersiz bir ID veriyoruz
        m.setMedicineId(UUID.randomUUID().toString());
        // ---------------------------------

        m.setName(dto.getName());
        m.setType(dto.getType());

        Medicine saved = medicineRepository.save(m);
        return medicineMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MedicineResponseDTO updateMedicine(String medicineId, MedicineUpdateRequestDTO dto) {

        Medicine existing = medicineRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("Medicine bulunamadı: " + medicineId));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            Optional<Medicine> sameName = medicineRepository.findByName(dto.getName());
            if (sameName.isPresent() && !sameName.get().getMedicineId().equals(medicineId)) {
                throw new IllegalStateException("Bu isim başka bir medicine için zaten kullanılıyor: " + dto.getName());
            }
            existing.setName(dto.getName());
        }

        if (dto.getType() != null && !dto.getType().trim().isEmpty()) {
            existing.setType(dto.getType());
        }

        Medicine saved = medicineRepository.save(existing);
        return medicineMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMedicine(String medicineId) {
        // PK = medicineId olduğundan deleteById da çalışır ama yine de domain methodla gidelim
        Medicine existing = medicineRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen medicine bulunamadı: " + medicineId));

        medicineRepository.delete(existing);
    }
}
