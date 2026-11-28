
package com.awd.medicalrecordservice.repository;

import com.awd.medicalrecordservice.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccinationRepository
        extends JpaRepository<Vaccination, Long> {

    List<Vaccination> findByMedicalRecord_Id(Long medicalRecordId);
}
