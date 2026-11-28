
package com.awd.medicalrecordservice.repository;

import com.awd.medicalrecordservice.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByAnimalId(Long animalId);

    boolean existsByAnimalId(Long animalId);
}
