
package com.awd.medicalrecordservice.service;

import com.awd.medicalrecordservice.entity.MedicalRecord;
import com.awd.medicalrecordservice.entity.Prescription;
import com.awd.medicalrecordservice.repository.MedicalRecordRepository;
import com.awd.medicalrecordservice.repository.PrescriptionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    // CREATE
    public Prescription create(
            Long medicalRecordId,
            Prescription prescription) {

        MedicalRecord record = medicalRecordRepository
                .findById(medicalRecordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dossier medical introuvable"
                ));

        if (prescription.getMedicationName() == null ||
                prescription.getMedicationName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nom du medicament est obligatoire"
            );
        }

        prescription.setId(null);
        prescription.setMedicalRecord(record);

        return prescriptionRepository.save(prescription);
    }

    // READ ALL
    public List<Prescription> findAll() {
        return prescriptionRepository.findAll();
    }

    // READ BY ID
    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prescription introuvable"
                ));
    }

    // READ BY MEDICAL RECORD
    public List<Prescription> findByMedicalRecordId(Long medicalRecordId) {
        return prescriptionRepository
                .findByMedicalRecord_Id(medicalRecordId);
    }

    // READ BY CONSULTATION
    public List<Prescription> findByConsultationId(Long consultationId) {
        return prescriptionRepository
                .findByConsultationId(consultationId);
    }

    // UPDATE
    public Prescription update(Long id, Prescription updated) {

        Prescription existing = findById(id);

        if (updated.getMedicationName() == null ||
                updated.getMedicationName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nom du medicament est obligatoire"
            );
        }

        existing.setVeterinarianId(updated.getVeterinarianId());
        existing.setConsultationId(updated.getConsultationId());
        existing.setMedicationName(updated.getMedicationName());
        existing.setDosage(updated.getDosage());
        existing.setFrequency(updated.getFrequency());
        existing.setDuration(updated.getDuration());
        existing.setPrescriptionDate(updated.getPrescriptionDate());
        existing.setInstructions(updated.getInstructions());

        return prescriptionRepository.save(existing);
    }

    // DELETE
    public void delete(Long id) {
        Prescription existing = findById(id);
        prescriptionRepository.delete(existing);
    }
}
