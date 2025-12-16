package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordCreateRequestDTO;
import com.petnabiz.petnabiz.dto.request.medicalrecord.MedicalRecordUpdateRequestDTO;
import com.petnabiz.petnabiz.dto.response.medicalrecord.MedicalRecordResponseDTO;
import com.petnabiz.petnabiz.mapper.MedicalRecordMapper;
import com.petnabiz.petnabiz.model.MedicalRecord;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.MedicalRecordRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.MedicalRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    PetRepository petRepository,
                                    VeterinaryRepository veterinaryRepository,
                                    MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
        this.veterinaryRepository = veterinaryRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

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

        if (dto.getPetId() == null || dto.getPetId().isBlank()) {
            throw new IllegalArgumentException("MedicalRecord için petId zorunlu.");
        }
        if (dto.getVetId() == null || dto.getVetId().isBlank()) {
            throw new IllegalArgumentException("MedicalRecord için vetId zorunlu.");
        }
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("MedicalRecord için date zorunlu.");
        }

        Pet pet = petRepository.findByPetId(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + dto.getPetId()));

        Veterinary vet = veterinaryRepository.findByVetId(dto.getVetId())
                .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + dto.getVetId()));

        MedicalRecord record = new MedicalRecord();

        if (dto.getRecordId() != null && !dto.getRecordId().isBlank()) {
            record.setRecordId(dto.getRecordId());
        }

        record.setDescription(dto.getDescription());
        record.setDate(dto.getDate());
        record.setPet(pet);
        record.setVeterinary(vet);

        MedicalRecord saved = medicalRecordRepository.save(record);
        return medicalRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MedicalRecordResponseDTO updateMedicalRecord(String recordId, MedicalRecordUpdateRequestDTO dto) {

        MedicalRecord existing = medicalRecordRepository.findByRecordId(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord bulunamadı: " + recordId));

        if (dto.getDate() != null) existing.setDate(dto.getDate());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());

        if (dto.getPetId() != null) {
            Pet newPet = petRepository.findByPetId(dto.getPetId())
                    .orElseThrow(() -> new IllegalArgumentException("Yeni pet bulunamadı: " + dto.getPetId()));
            existing.setPet(newPet);
        }

        if (dto.getVetId() != null) {
            Veterinary newVet = veterinaryRepository.findByVetId(dto.getVetId())
                    .orElseThrow(() -> new IllegalArgumentException("Yeni veterinary bulunamadı: " + dto.getVetId()));
            existing.setVeterinary(newVet);
        }

        MedicalRecord saved = medicalRecordRepository.save(existing);
        return medicalRecordMapper.toResponse(saved);
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
