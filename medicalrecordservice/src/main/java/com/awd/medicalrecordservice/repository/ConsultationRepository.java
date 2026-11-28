
package com.awd.medicalrecordservice.repository;

import com.awd.medicalrecordservice.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository
        extends JpaRepository<Consultation, Long> {

    List<Consultation> findByMedicalRecord_Id(Long medicalRecordId);

    List<Consultation> findByVeterinarianId(Long veterinarianId);
}
