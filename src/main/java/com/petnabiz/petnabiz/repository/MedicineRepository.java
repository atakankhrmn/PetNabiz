package com.petnabiz.petnabiz.repository;

import com.petnabiz.petnabiz.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {

    // 1) ID bazlı erişim
    Optional<Medicine> findByMedicineId(String medicineId);
    boolean existsByMedicineId(String medicineId);

    // 2) İsim bazlı aramalar
    Optional<Medicine> findByName(String name);
    List<Medicine> findByNameContainingIgnoreCase(String namePart);

    // 3) Type bazlı filtreler (tablet, syrup vs.)
    List<Medicine> findByTypeIgnoreCase(String type);

    // 4) Type + isim birlikte filtre (örn: sadece "tablet" olan ve adı "para" içerenler)
    List<Medicine> findByTypeIgnoreCaseAndNameContainingIgnoreCase(String type, String namePart);
}
