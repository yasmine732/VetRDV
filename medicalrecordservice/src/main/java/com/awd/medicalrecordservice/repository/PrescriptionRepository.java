
package com.awd.medicalrecordservice.repository;

import com.awd.medicalrecordservice.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByMedicalRecord_Id(Long medicalRecordId);

    List<Prescription> findByConsultationId(Long consultationId);
}
