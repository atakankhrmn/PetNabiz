package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {

    Optional<Medicine> findByMedicineId(String medicineId);
    boolean existsByMedicineId(String medicineId);

    Optional<Medicine> findByName(String name);

    List<Medicine> findByNameContainingIgnoreCase(String namePart);
    List<Medicine> findByTypeIgnoreCase(String type);

    List<Medicine> findByTypeIgnoreCaseAndNameContainingIgnoreCase(String type, String namePart);
}
