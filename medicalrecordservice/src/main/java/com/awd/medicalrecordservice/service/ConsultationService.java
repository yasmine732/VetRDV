
package com.awd.medicalrecordservice.service;

import com.awd.medicalrecordservice.entity.Consultation;
import com.awd.medicalrecordservice.entity.MedicalRecord;
import com.awd.medicalrecordservice.repository.ConsultationRepository;
import com.awd.medicalrecordservice.repository.MedicalRecordRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public ConsultationService(
            ConsultationRepository consultationRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.consultationRepository = consultationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    // CREATE
    public Consultation create(
            Long medicalRecordId,
            Consultation consultation) {

        MedicalRecord medicalRecord = medicalRecordRepository
                .findById(medicalRecordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dossier medical introuvable"
                ));

        consultation.setId(null);
        consultation.setMedicalRecord(medicalRecord);

        return consultationRepository.save(consultation);
    }

    // READ ALL
    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    // READ BY ID
    public Consultation findById(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Consultation introuvable"
                ));
    }

    // READ BY MEDICAL RECORD
    public List<Consultation> findByMedicalRecordId(
            Long medicalRecordId) {

        return consultationRepository
                .findByMedicalRecord_Id(medicalRecordId);
    }

    // READ BY VETERINARIAN
    public List<Consultation> findByVeterinarianId(
            Long veterinarianId) {

        return consultationRepository
                .findByVeterinarianId(veterinarianId);
    }

    // UPDATE
    public Consultation update(
            Long id,
            Consultation updatedConsultation) {

        Consultation existing = findById(id);

        existing.setVeterinarianId(
                updatedConsultation.getVeterinarianId());
        existing.setAppointmentId(
                updatedConsultation.getAppointmentId());
        existing.setConsultationDate(
                updatedConsultation.getConsultationDate());
        existing.setReason(updatedConsultation.getReason());
        existing.setDiagnosis(updatedConsultation.getDiagnosis());
        existing.setTreatment(updatedConsultation.getTreatment());
        existing.setNotes(updatedConsultation.getNotes());

        return consultationRepository.save(existing);
    }

    // DELETE
    public void delete(Long id) {
        Consultation existing = findById(id);
        consultationRepository.delete(existing);
    }
}
