
package com.awd.medicalrecordservice.service;

import com.awd.medicalrecordservice.entity.MedicalRecord;
import com.awd.medicalrecordservice.entity.Vaccination;
import com.awd.medicalrecordservice.repository.MedicalRecordRepository;
import com.awd.medicalrecordservice.repository.VaccinationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public VaccinationService(
            VaccinationRepository vaccinationRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    // CREATE
    public Vaccination create(
            Long medicalRecordId,
            Vaccination vaccination) {

        MedicalRecord record = medicalRecordRepository
                .findById(medicalRecordId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dossier medical introuvable"
                ));

        if (vaccination.getVaccineName() == null ||
                vaccination.getVaccineName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nom du vaccin est obligatoire"
            );
        }

        vaccination.setId(null);
        vaccination.setMedicalRecord(record);

        return vaccinationRepository.save(vaccination);
    }

    // READ ALL
    public List<Vaccination> findAll() {
        return vaccinationRepository.findAll();
    }

    // READ BY ID
    public Vaccination findById(Long id) {
        return vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vaccination introuvable"
                ));
    }

    // READ BY MEDICAL RECORD
    public List<Vaccination> findByMedicalRecordId(Long medicalRecordId) {
        return vaccinationRepository
                .findByMedicalRecord_Id(medicalRecordId);
    }

    // UPDATE
    public Vaccination update(Long id, Vaccination updated) {

        Vaccination existing = findById(id);

        if (updated.getVaccineName() == null ||
                updated.getVaccineName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le nom du vaccin est obligatoire"
            );
        }

        existing.setVaccineName(updated.getVaccineName());
        existing.setVaccinationDate(updated.getVaccinationDate());
        existing.setNextDueDate(updated.getNextDueDate());
        existing.setBatchNumber(updated.getBatchNumber());
        existing.setVeterinarianId(updated.getVeterinarianId());
        existing.setNotes(updated.getNotes());

        return vaccinationRepository.save(existing);
    }

    // DELETE
    public void delete(Long id) {
        Vaccination existing = findById(id);
        vaccinationRepository.delete(existing);
    }
}
