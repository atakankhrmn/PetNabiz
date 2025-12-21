package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Appointment;
import com.petnabiz.petnabiz.model.Pet;
import com.petnabiz.petnabiz.model.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    // 1) ID bazlı erişim (JpaRepository.findById ile aynı işi yapar ama isim daha okunaklı)
    Optional<Appointment> findByAppointmentId(String appointmentId);


    // 2) Tarihe göre randevular
    List<Appointment> findByDate(LocalDate date);

    // Belirli bir günde saat sırasına göre randevular (takvim ekranı için ideal)
    List<Appointment> findByDateOrderByTimeAsc(LocalDate date);


    // 3) PET bazlı sorgular
    // Pet entity ile
    List<Appointment> findByPet(Pet pet);

    // Pet ID ile
    List<Appointment> findByPet_PetId(String petId);


    // 4) VETERINARY bazlı sorgular
    // Vet entity ile
    List<Appointment> findByVeterinary(Veterinary veterinary);

    // Vet ID ile tüm randevular
    List<Appointment> findByVeterinary_VetId(String vetId);

    // Vet + tarih aralığı (örneğin "bu vet için bu hafta" gibi)
    List<Appointment> findByVeterinary_VetIdAndDateBetween(
            String vetId,
            LocalDate startDate,
            LocalDate endDate
    );



    // 6) Müsaitlik / çakışma kontrolü (availability check)
    // Belirli vet, belirli gün, belirli saat aralığında randevu VAR MI?
    boolean existsByVeterinary_VetIdAndDateAndTimeBetween(
            String vetId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );

    // Aynı vet, aynı gün, aynı saat için kayıt var mı? (tam slot çakışması)
    boolean existsByVeterinary_VetIdAndDateAndTime(
            String vetId,
            LocalDate date,
            LocalTime time
    );


    // 7) Klinik bazlı sorgu (veterinary -> clinic -> clinicId zinciri)
    // Klinik için, iki tarih arasında tüm randevular
    List<Appointment> findByVeterinary_Clinic_ClinicIdAndDateBetween(
            String clinicId,
            LocalDate startDate,
            LocalDate endDate
    );


}
