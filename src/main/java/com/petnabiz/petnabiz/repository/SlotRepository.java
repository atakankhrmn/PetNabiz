package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    // Bir veterinerin bir günde boş slotları
    List<Slot> findByVeterinary_VetIdAndDateAndIsBookedFalse(
            String vetId,
            LocalDate date
    );

    // Tek bir slotu (vet + date + time)
    Optional<Slot> findByVeterinary_VetIdAndDateAndTime(
            String vetId,
            LocalDate date,
            LocalTime time
    );

    // Slotu atomik şekilde rezerve et (çakışma engelli)
    @Modifying
    @Query("""
        UPDATE Slot s
        SET s.isBooked = true
        WHERE s.slotId = :slotId
          AND s.isBooked = false
    """)
    int bookSlot(@Param("slotId") Long slotId);


    //isBooked opsiyonel onu silersek booklanmış slotlar da görünecek
    @Query("""
    SELECT s
    FROM Slot s
    JOIN s.veterinary v
    JOIN v.clinic c
    WHERE s.date BETWEEN :startDate AND :endDate
      AND c.city = :city
      AND c.district = :district
      AND s.isBooked = false
    ORDER BY s.date, s.time
    """)
    List<Slot> findAvailableSlotsByDateRangeCityDistrict(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("city") String city,
            @Param("district") String district
    );


}
