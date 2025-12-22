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
    List<Slot> findByVeterinary_VetIdAndDate(
            String vetId,
            LocalDate date
    );

    Optional<Slot> findBySlotId(Long slotId);

    // Slotu atomik şekilde rezerve et (çakışma engelli)
    @Modifying
    @Query("""
        UPDATE Slot s
        SET s.isBooked = true
        WHERE s.slotId = :slotId
          AND s.isBooked = false
    """)
    int bookSlot(@Param("slotId") Long slotId);


    @Query("""
    SELECT s
    FROM Slot s
    JOIN s.veterinary v
    JOIN v.clinic c
    WHERE s.isBooked = false
      AND s.date BETWEEN :startDate AND :endDate
      AND c.city = :city
      AND c.district = :district
      AND (
            s.date > :today
            OR (s.date = :today AND s.time > :now)
          )
""")
    List<Slot> findAvailableSlotsByDateRangeCityDistrict(
            LocalDate startDate,
            LocalDate endDate,
            String city,
            String district,
            LocalDate today,
            LocalTime now
    );


}
