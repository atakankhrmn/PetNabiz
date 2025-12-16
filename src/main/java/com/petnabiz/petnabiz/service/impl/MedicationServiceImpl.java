package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.medication.MedicationCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medication.MedicationUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medication.MedicationResponseDTO;
import com.petnabiz.petnabiz.mapper.MedicationMapper;
import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.model.Medication;
import com.petnabiz.petnabiz.model.Medicine;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.repository.MedicalRecordRepository;
import com.petnabiz.petnabiz.repository.MedicationRepository;
import com.petnabiz.petnabiz.repository.MedicineRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.service.MedicationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("medicationService") // @PreAuthorize içinde @medicationService için net bean adı
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicineRepository medicineRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final MedicationMapper medicationMapper;

    public MedicationServiceImpl(MedicationRepository medicationRepository,
                                 MedicineRepository medicineRepository,
                                 MedicalRecordRepository medicalRecordRepository,
                                 PetRepository petRepository,
                                 MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicineRepository = medicineRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
        this.medicationMapper = medicationMapper;
    }

    // ---------------------------
    // Security helpers (SpEL için)
    // ---------------------------

    @Override
    public boolean isPetOwnedBy(String ownerEmail, String petId) {
        if (ownerEmail == null || petId == null || petId.isBlank()) return false;

        Pet pet = petRepository.findByPetId(petId).orElse(null);
        if (pet == null) return false;

        if (pet.getOwner() == null ||
                pet.getOwner().getUser() == null ||
                pet.getOwner().getUser().getEmail() == null) {
            return false;
        }

        return ownerEmail.equalsIgnoreCase(pet.getOwner().getUser().getEmail());
    }

    @Override
    public boolean isRecordOwnedBy(String ownerEmail, String recordId) {
        if (ownerEmail == null || recordId == null || recordId.isBlank()) return false;

        MedicalRecord r = medicalRecordRepository.findByRecordId(recordId).orElse(null);
        if (r == null) return false;
        if (r.getPet() == null) return false;

        Pet pet = r.getPet();

        if (pet.getOwner() == null ||
                pet.getOwner().getUser() == null ||
                pet.getOwner().getUser().getEmail() == null) {
            return false;
        }

        return ownerEmail.equalsIgnoreCase(pet.getOwner().getUser().getEmail());
    }

    @Override
    public boolean isMedicationOwnedBy(String ownerEmail, String medicationId) {
        if (ownerEmail == null || medicationId == null || medicationId.isBlank()) return false;

        Medication m = medicationRepository.findByMedicationId(medicationId).orElse(null);
        if (m == null) return false;

        // Medication -> MedicalRecord -> Pet -> Owner -> User -> Email
        if (m.getMedicalRecord() == null) return false;
        MedicalRecord r = m.getMedicalRecord();
        if (r.getPet() == null) return false;

        Pet pet = r.getPet();

        if (pet.getOwner() == null ||
                pet.getOwner().getUser() == null ||
                pet.getOwner().getUser().getEmail() == null) {
            return false;
        }

        return ownerEmail.equalsIgnoreCase(pet.getOwner().getUser().getEmail());
    }

    // ---------------------------
    // Queries
    // ---------------------------

    @Override
    public List<MedicationResponseDTO> getAllMedications() {
        return medicationRepository.findAll().stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public MedicationResponseDTO getMedicationById(String medicationId) {
        Medication m = medicationRepository.findByMedicationId(medicationId)
                .orElseThrow(() -> new EntityNotFoundException("Medication bulunamadı: " + medicationId));
        return medicationMapper.toResponse(m);
    }

    @Override
    public List<MedicationResponseDTO> getMedicationsByMedicineId(String medicineId) {
        return medicationRepository.findByMedicine_MedicineId(medicineId).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponseDTO> searchByMedicineName(String namePart) {
        return medicationRepository.findByMedicine_NameContainingIgnoreCase(namePart).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponseDTO> getMedicationsByMedicineType(String type) {
        return medicationRepository.findByMedicine_TypeIgnoreCase(type).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponseDTO> getActiveMedicationsOn(LocalDate date) {
        return medicationRepository.findByStartLessThanEqualAndEndGreaterThanEqual(date, date).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponseDTO> getMedicationsBetween(LocalDate start, LocalDate end) {
        return medicationRepository.findByStartGreaterThanEqualAndEndLessThanEqual(start, end).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponseDTO> getMedicationsByMedicalRecordId(String recordId) {
        medicalRecordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord bulunamadı: " + recordId));

        return medicationRepository.findByMedicalRecord_RecordId(recordId).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponseDTO> getMedicationsByPetId(String petId) {
        petRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + petId));

        return medicationRepository.findByMedicalRecord_Pet_PetId(petId).stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    // ---------------------------
    // Mutations
    // ---------------------------

    @Override
    @Transactional
    public MedicationResponseDTO createMedication(MedicationCreateRequestDTO dto) {

        if (dto.getMedicineId() == null || dto.getMedicineId().isBlank()) {
            throw new IllegalArgumentException("Medication için medicineId zorunlu.");
        }
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Medication için start ve end zorunlu.");
        }
        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new IllegalArgumentException("End tarihi start tarihinden önce olamaz.");
        }

        Medicine medicine = medicineRepository.findByMedicineId(dto.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine bulunamadı: " + dto.getMedicineId()));

        MedicalRecord record = null;
        if (dto.getRecordId() != null && !dto.getRecordId().isBlank()) {
            record = medicalRecordRepository.findByRecordId(dto.getRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("MedicalRecord bulunamadı: " + dto.getRecordId()));
        }

        Medication m = new Medication();

        if (dto.getMedicationId() != null && !dto.getMedicationId().isBlank()) {
            m.setMedicationId(dto.getMedicationId());
        }

        m.setInstructions(dto.getInstructions());
        m.setStart(dto.getStart());
        m.setEnd(dto.getEnd());
        m.setMedicine(medicine);

        if (record != null) {
            m.setMedicalRecord(record);
        }

        Medication saved = medicationRepository.save(m);
        return medicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MedicationResponseDTO updateMedication(String medicationId, MedicationUpdateRequestDTO dto) {

        Medication existing = medicationRepository.findByMedicationId(medicationId)
                .orElseThrow(() -> new EntityNotFoundException("Medication bulunamadı: " + medicationId));

        if (dto.getInstructions() != null) existing.setInstructions(dto.getInstructions());
        if (dto.getStart() != null) existing.setStart(dto.getStart());
        if (dto.getEnd() != null) existing.setEnd(dto.getEnd());

        if (existing.getStart() != null && existing.getEnd() != null &&
                existing.getEnd().isBefore(existing.getStart())) {
            throw new IllegalArgumentException("End tarihi start tarihinden önce olamaz.");
        }

        if (dto.getMedicineId() != null) {
            Medicine newMedicine = medicineRepository.findByMedicineId(dto.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine bulunamadı: " + dto.getMedicineId()));
            existing.setMedicine(newMedicine);
        }

        if (dto.getRecordId() != null) {
            if (dto.getRecordId().isBlank()) {
                existing.setMedicalRecord(null);
            } else {
                MedicalRecord record = medicalRecordRepository.findByRecordId(dto.getRecordId())
                        .orElseThrow(() -> new IllegalArgumentException("MedicalRecord bulunamadı: " + dto.getRecordId()));
                existing.setMedicalRecord(record);
            }
        }

        Medication saved = medicationRepository.save(existing);
        return medicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMedication(String medicationId) {
        // PK karışıklığı riskini bitir:
        Medication existing = medicationRepository.findByMedicationId(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen medication bulunamadı: " + medicationId));

        medicationRepository.delete(existing);
    }
}
