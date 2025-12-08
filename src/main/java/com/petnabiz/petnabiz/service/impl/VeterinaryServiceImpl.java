package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.ClinicRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.VeterinaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinaryServiceImpl implements VeterinaryService {

    private final VeterinaryRepository veterinaryRepository;
    private final ClinicRepository clinicRepository;

    public VeterinaryServiceImpl(VeterinaryRepository veterinaryRepository,
                                 ClinicRepository clinicRepository) {
        this.veterinaryRepository = veterinaryRepository;
        this.clinicRepository = clinicRepository;
    }

    @Override
    public List<Veterinary> getAllVeterinaries() {
        return veterinaryRepository.findAll();
    }

    @Override
    public Optional<Veterinary> getVeterinaryById(String vetId) {
        return veterinaryRepository.findByVetId(vetId);
        // veya: return veterinaryRepository.findById(vetId);
    }

    @Override
    public List<Veterinary> searchByFirstName(String firstNamePart) {
        return veterinaryRepository.findByFirstNameContainingIgnoreCase(firstNamePart);
    }

    @Override
    public List<Veterinary> searchByLastName(String lastNamePart) {
        return veterinaryRepository.findByLastNameContainingIgnoreCase(lastNamePart);
    }

    @Override
    public Optional<Veterinary> getByPhoneNumber(String phoneNumber) {
        return veterinaryRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Veterinary> searchByCertificate(String certificatePart) {
        return veterinaryRepository.findByCertificateContainingIgnoreCase(certificatePart);
    }

    @Override
    public List<Veterinary> getVeterinariesByClinicId(String clinicId) {
        // Önce klinik var mı diye bak (opsiyonel ama hatayı daha anlamlı yapar)
        clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic bulunamadı: " + clinicId));

        return veterinaryRepository.findByClinic_ClinicId(clinicId);
    }

    @Override
    public Veterinary createVeterinary(Veterinary veterinary) {

        // Basit zorunlu alan kontrolleri
        if (veterinary.getVetId() == null || veterinary.getVetId().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary ID boş olamaz.");
        }
        if (veterinary.getFirstName() == null || veterinary.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary firstName boş olamaz.");
        }
        if (veterinary.getLastName() == null || veterinary.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary lastName boş olamaz.");
        }
        if (veterinary.getPhoneNumber() == null || veterinary.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinary phoneNumber boş olamaz.");
        }

        // Aynı ID'de vet var mı?
        if (veterinaryRepository.existsByVetId(veterinary.getVetId())) {
            throw new IllegalStateException("Bu vetId ile zaten bir veterinary kayıtlı: " + veterinary.getVetId());
        }

        // Klinik set edildiyse, gerçekten var mı?
        if (veterinary.getClinic() != null && veterinary.getClinic().getClinicId() != null) {
            String clinicId = veterinary.getClinic().getClinicId();

            Clinic clinic = clinicRepository.findByClinicId(clinicId)
                    .orElseThrow(() -> new IllegalArgumentException("Clinic bulunamadı: " + clinicId));

            veterinary.setClinic(clinic); // managed entity
        }

        return veterinaryRepository.save(veterinary);
    }

    @Override
    public Veterinary updateVeterinary(String vetId, Veterinary updatedVeterinary) {
        Veterinary existing = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + vetId));

        // İsimler
        if (updatedVeterinary.getFirstName() != null && !updatedVeterinary.getFirstName().trim().isEmpty()) {
            existing.setFirstName(updatedVeterinary.getFirstName());
        }
        if (updatedVeterinary.getLastName() != null && !updatedVeterinary.getLastName().trim().isEmpty()) {
            existing.setLastName(updatedVeterinary.getLastName());
        }

        // Telefon
        if (updatedVeterinary.getPhoneNumber() != null && !updatedVeterinary.getPhoneNumber().trim().isEmpty()) {
            existing.setPhoneNumber(updatedVeterinary.getPhoneNumber());
        }

        // Adres
        if (updatedVeterinary.getAddress() != null) {
            existing.setAddress(updatedVeterinary.getAddress());
        }

        // Sertifika
        if (updatedVeterinary.getCertificate() != null) {
            existing.setCertificate(updatedVeterinary.getCertificate());
        }

        // Klinik değişimi
        if (updatedVeterinary.getClinic() != null &&
                updatedVeterinary.getClinic().getClinicId() != null) {

            String newClinicId = updatedVeterinary.getClinic().getClinicId();
            Clinic newClinic = clinicRepository.findByClinicId(newClinicId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni clinic bulunamadı: " + newClinicId));

            existing.setClinic(newClinic);
        }

        return veterinaryRepository.save(existing);
    }

    @Override
    public void deleteVeterinary(String vetId) {
        boolean exists = veterinaryRepository.existsByVetId(vetId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen veterinary bulunamadı: " + vetId);
        }

        veterinaryRepository.deleteById(vetId);
    }
}
