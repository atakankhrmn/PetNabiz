package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medication.MedicationUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;
import com.petnabiz.petnabiz.mapper.MedicalRecordMapper;
import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.model.Medication;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.MedicalRecordRepository;
import com.petnabiz.petnabiz.repository.MedicationRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.MedicalRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

@Service("medicalRecordService") // @PreAuthorize içinde @medicalRecordService diye çağıracağız
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final MedicationRepository medicationRepository;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    PetRepository petRepository,
                                    VeterinaryRepository veterinaryRepository,
                                    MedicalRecordMapper medicalRecordMapper, MedicationRepository medicationRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.medicalRecordMapper = medicalRecordMapper;
        this.medicationRepository = medicationRepository;
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

    // ---------------------------
    // CRUD
    // ---------------------------

    @Override
    public List<MedicalRecordResponseDTO> getAllMedicalRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(medicalRecordMapper::toResponse)
                .toList();
    }

    @Override
    public MedicalRecordResponseDTO getMedicalRecordById(String recordId) {
        MedicalRecord r = medicalRecordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord bulunamadı: " + recordId));
        return medicalRecordMapper.toResponse(r);
    }

    @Override
    public List<MedicalRecordResponseDTO> getMedicalRecordsByPetId(String petId) {
        return medicalRecordRepository.findByPet_PetId(petId)
                .stream()
                .map(medicalRecordMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicalRecordResponseDTO> getMedicalRecordsByVeterinaryId(String vetId) {
        return medicalRecordRepository.findByVeterinary_VetId(vetId)
                .stream()
                .map(medicalRecordMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicalRecordResponseDTO> getMedicalRecordsByDate(LocalDate date) {
        return medicalRecordRepository.findByDate(date)
                .stream()
                .map(medicalRecordMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicalRecordResponseDTO> getMedicalRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return medicalRecordRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(medicalRecordMapper::toResponse)
                .toList();
    }



    @Override
    @Transactional
    public MedicalRecordResponseDTO createMedicalRecord(MedicalRecordCreateRequestDTO dto) {

        // 1. Validasyonlar
        if (dto.getPetId() == null || dto.getPetId().isBlank()) {
            throw new IllegalArgumentException("MedicalRecord için petId zorunlu.");
        }
        if (dto.getVetId() == null || dto.getVetId().isBlank()) {
            throw new IllegalArgumentException("MedicalRecord için vetId zorunlu.");
        }
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("MedicalRecord için date zorunlu.");
        }

        // 2. Pet ve Vet Bulma
        Pet pet = petRepository.findByPetId(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + dto.getPetId()));

        Veterinary vet = veterinaryRepository.findByVetId(dto.getVetId())
                .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + dto.getVetId()));

        MedicalRecord record = new MedicalRecord();

        // --- DEĞİŞİKLİK BURADA ---
        // Frontend'den ID gelmesini beklemiyoruz, biz üretiyoruz:
        record.setRecordId(UUID.randomUUID().toString());
        // -------------------------

        record.setDescription(dto.getDescription());
        record.setDate(dto.getDate());
        record.setPet(pet);
        record.setVeterinary(vet);

        MedicalRecord saved = medicalRecordRepository.save(record);
        return medicalRecordMapper.toResponse(saved);
    }

    // Service sınıfının başına MedicationRepository'yi inject etmeyi unutma:
    // private final MedicationRepository medicationRepository;

    @Override
    @Transactional
    public MedicalRecordResponseDTO updateMedicalRecord(String recordId, MedicalRecordUpdateRequestDTO dto) {

        // 1. Ana Kaydı (MedicalRecord) Bul
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("MedicalRecord bulunamadı: " + recordId));

        // 2. Ana Bilgileri Güncelle
        if (dto.getDescription() != null) record.setDescription(dto.getDescription());
        if (dto.getDate() != null) record.setDate(dto.getDate());

        // 3. İLAÇLARI GÜNCELLEME MANTIĞI (Cascading Update)
        if (dto.getMedications() != null && !dto.getMedications().isEmpty()) {

            for (MedicationUpdateRequestDTO medDto : dto.getMedications()) {
                // Eğer ilacın bir ID'si varsa, mevcut ilacı bulup güncelliyoruz
                if (medDto.getMedicationId() != null) { // React tarafında medicineId değil medicationId kullanmıştık dikkat

                    Medication existingMed = medicationRepository.findByMedicationId(medDto.getMedicationId())
                            .orElse(null); // Bulamazsa null dönsün, hata patlamasın

                    if (existingMed != null) {
                        existingMed.setInstructions(medDto.getInstructions());
                        existingMed.setStart(medDto.getStart());
                        existingMed.setEnd(medDto.getEnd());

                        // İlaç türü değiştiyse onu da güncellemek gerekebilir ama şimdilik detayları güncelliyoruz
                        medicationRepository.save(existingMed);
                    }
                }
                // Eğer ID'si yoksa bu yeni eklenen bir ilaçtır (Opsiyonel: Create mantığı buraya eklenebilir)
            }
        }

        // 4. Kaydet ve Döndür
        MedicalRecord saved = medicalRecordRepository.save(record);
        return medicalRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMedicalRecord(String recordId) {
        // existsById/deleteById yerine güvenli silme:
        MedicalRecord existing = medicalRecordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen MedicalRecord bulunamadı: " + recordId));

        medicalRecordRepository.delete(existing);
    }
}
