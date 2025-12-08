package com.petnabiz.petnabiz.service;

import com.petnabiz.petnabiz.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    // Tüm randevuları getir
    List<Appointment> getAllAppointments();

    // ID ile randevu bul
    Optional<Appointment> getAppointmentById(String appointmentId);

    // Belirli bir pet için tüm randevular
    List<Appointment> getAppointmentsByPetId(String petId);

    // Belirli bir vet için tüm randevular
    List<Appointment> getAppointmentsByVeterinaryId(String vetId);

    // Vet + tarih aralığına göre randevular (takvim/rapor)
    List<Appointment> getAppointmentsByVeterinaryAndDateRange(
            String vetId,
            LocalDate startDate,
            LocalDate endDate
    );

    // Clinicteki tüm appointmentları listeler
    public List<Appointment> getAppointmentsByClinicId(String clinicId);

    // Yeni randevu oluştur (pet + vet atanmış şekilde gelir)
    Appointment createAppointment(Appointment appointment);

    // Randevu güncelle (örnek: tarih/saat/pet/vet değişimi)
    Appointment updateAppointment(String appointmentId, Appointment updatedAppointment);

    // Randevu sil
    void deleteAppointment(String appointmentId);
}
