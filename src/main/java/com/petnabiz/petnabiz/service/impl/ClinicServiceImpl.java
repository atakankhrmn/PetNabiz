package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Clinic;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.ClinicRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.ClinicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final VeterinaryRepository veterinaryRepository;

    public ClinicServiceImpl(ClinicRepository clinicRepository,
                             VeterinaryRepository veterinaryRepository) {
        this.clinicRepository = clinicRepository;
        this.veterinaryRepository = veterinaryRepository;
    }

    @Override
    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    @Override
    public Optional<Clinic> getClinicById(String clinicId) {
        return clinicRepository.findByClinicId(clinicId);
        // veya: return clinicRepository.findById(clinicId);
    }

    @Override
    public List<Clinic> searchClinicsByName(String namePart) {
        return clinicRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    public Optional<Clinic> getClinicByEmail(String email) {
        return clinicRepository.findByEmail(email);
    }

    @Override
    public Clinic createClinic(Clinic clinic) {
        // Email zaten var mı?
        if (clinicRepository.existsByEmail((clinic.getEmail()))) {
            throw new IllegalArgumentException("Bu email ile zaten bir klinik kayıtlı.");
        }

        return clinicRepository.save(clinic);
    }

    @Override
    public Clinic updateClinic(String clinicId, Clinic updatedClinic) {
        Clinic existing = clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic bulunamadı: " + clinicId));

        // Modelde olan alanları güncelle
        existing.setName(updatedClinic.getName());
        existing.setAddress(updatedClinic.getAddress());
        existing.setPhone(updatedClinic.getPhone());
        existing.setEmail(updatedClinic.getEmail());

        return clinicRepository.save(existing);
    }

    @Override
    public void deleteClinic(String clinicId) {
        boolean exists = clinicRepository.existsByClinicId(clinicId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen klinik bulunamadı: " + clinicId);
        }

        clinicRepository.deleteById(clinicId);
    }

    @Override
    public List<Veterinary> getVeterinariesByClinic(String clinicId) {
        // Önce klinik var mı?
        clinicRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic bulunamadı: " + clinicId));

        // VeterinaryRepository üzerinden çekiyoruz
        return veterinaryRepository.findByClinic_ClinicId(clinicId);
    }
}
