package com.petnabiz.petnabiz.service.impl;

import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import com.petnabiz.petnabiz.repository.AppointmentRepository;
import com.petnabiz.petnabiz.repository.PetRepository;
import com.petnabiz.petnabiz.repository.VeterinaryRepository;
import com.petnabiz.petnabiz.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PetRepository petRepository,
                                  VeterinaryRepository veterinaryRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.veterinaryRepository = veterinaryRepository;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId);
        // veya: return appointmentRepository.findById(appointmentId);
    }

    @Override
    public List<Appointment> getAppointmentsByPetId(String petId) {
        return appointmentRepository.findByPet_PetId(petId);
    }

    @Override
    public List<Appointment> getAppointmentsByVeterinaryId(String vetId) {
        return appointmentRepository.findByVeterinary_VetId(vetId);
    }

    public List<Appointment> getAppointmentsByClinicId(String clinicId) {

        List<Veterinary> vetList = veterinaryRepository.findByClinic_ClinicId(clinicId);

        List<Appointment> allAppointments = new ArrayList<>();

        for (Veterinary vet : vetList) {
            List<Appointment> appointments = appointmentRepository.findByVeterinary_VetId(vet.getVetId());
            allAppointments.addAll(appointments);
        }

        return allAppointments;
    }


    @Override
    public List<Appointment> getAppointmentsByVeterinaryAndDateRange(
            String vetId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return appointmentRepository.findByVeterinary_VetIdAndDateBetween(
                vetId, startDate, endDate
        );
    }

    @Override
    public Appointment createAppointment(Appointment appointment) {
        /*
         * Beklediğimiz şey:
         * - appointment.getPet().getPetId() dolu olacak
         * - appointment.getVeterinary().getVetId() dolu olacak
         * - appointment.getDate(), appointment.getTime() dolu olacak
         *
         * Bu method:
         *  1) Pet gerçekten var mı kontrol eder
         *  2) Vet gerçekten var mı kontrol eder
         *  3) Vet'in aynı gün / aynı saatte randevusu var mı bakar (çakışma)
         *  4) Managed entity'lerle appointment'i kaydeder
         */

        if (appointment.getPet() == null || appointment.getPet().getPetId() == null) {
            throw new IllegalArgumentException("Appointment için pet bilgisi zorunlu.");
        }
        if (appointment.getVeterinary() == null || appointment.getVeterinary().getVetId() == null) {
            throw new IllegalArgumentException("Appointment için veterinary bilgisi zorunlu.");
        }

        String petId = appointment.getPet().getPetId();
        String vetId = appointment.getVeterinary().getVetId();

        Pet pet = petRepository.findByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet bulunamadı: " + petId));

        Veterinary vet = veterinaryRepository.findByVetId(vetId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinary bulunamadı: " + vetId));

        LocalDate date = appointment.getDate();
        LocalTime time = appointment.getTime();

        if (date == null || time == null) {
            throw new IllegalArgumentException("Appointment için date ve time zorunludur.");
        }

        // Basit çakışma kontrolü: aynı vet, aynı gün, aynı saat
        boolean exists = appointmentRepository
                .existsByVeterinary_VetIdAndDateAndTime(vetId, date, time);

        if (exists) {
            throw new IllegalStateException(
                    "Bu tarih ve saatte veteriner için zaten bir randevu mevcut."
            );
        }

        // Managed entity'leri set et
        appointment.setPet(pet);
        appointment.setVeterinary(vet);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment updateAppointment(String appointmentId, Appointment updatedAppointment) {
        Appointment existing = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment bulunamadı: " + appointmentId));

        // Temel alanlar: tarih & saat (modelindeki alan isimlerine göre)
        if (updatedAppointment.getDate() != null) {
            existing.setDate(updatedAppointment.getDate());
        }
        if (updatedAppointment.getTime() != null) {
            existing.setTime(updatedAppointment.getTime());
        }

        // Pet değişimi isteniyorsa:
        if (updatedAppointment.getPet() != null && updatedAppointment.getPet().getPetId() != null) {
            String newPetId = updatedAppointment.getPet().getPetId();
            Pet newPet = petRepository.findByPetId(newPetId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni pet bulunamadı: " + newPetId));
            existing.setPet(newPet);
        }

        // Vet değişimi isteniyorsa:
        if (updatedAppointment.getVeterinary() != null &&
                updatedAppointment.getVeterinary().getVetId() != null) {

            String newVetId = updatedAppointment.getVeterinary().getVetId();
            Veterinary newVet = veterinaryRepository.findByVetId(newVetId)
                    .orElseThrow(() -> new IllegalArgumentException("Yeni veterinary bulunamadı: " + newVetId));
            existing.setVeterinary(newVet);
        }

        // Burada istersen reason / status gibi alanlar da varsa onları da benzer şekilde set edebilirsin:
        // existing.setReason(updatedAppointment.getReason());
        // existing.setStatus(updatedAppointment.getStatus());

        return appointmentRepository.save(existing);
    }

    @Override
    public void deleteAppointment(String appointmentId) {
        boolean exists = appointmentRepository.existsById(appointmentId);
        if (!exists) {
            throw new IllegalArgumentException("Silinmek istenen appointment bulunamadı: " + appointmentId);
        }

        appointmentRepository.deleteById(appointmentId);
    }
}
