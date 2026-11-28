
package com.awd.medicalrecordservice.service;

import com.awd.medicalrecordservice.entity.MedicalRecord;
import com.awd.medicalrecordservice.repository.MedicalRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repository;

    public MedicalRecordService(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public MedicalRecord create(MedicalRecord medicalRecord) {
        if (medicalRecord.getAnimalId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "animalId est obligatoire"
            );
        }

        if (repository.existsByAnimalId(medicalRecord.getAnimalId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un dossier medical existe deja pour cet animal"
            );
        }

        medicalRecord.setId(null);
        return repository.save(medicalRecord);
    }

    // READ ALL
    public List<MedicalRecord> findAll() {
        return repository.findAll();
    }

    // READ BY ID
    public MedicalRecord findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Dossier medical introuvable"
                ));
    }

    // READ BY ANIMAL ID
    public MedicalRecord findByAnimalId(Long animalId) {
        return repository.findByAnimalId(animalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Aucun dossier pour cet animal"
                ));
    }

    // UPDATE
    public MedicalRecord update(Long id, MedicalRecord updatedRecord) {
        MedicalRecord existing = findById(id);

        if (updatedRecord.getAnimalId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "animalId est obligatoire"
            );
        }

        if (!existing.getAnimalId().equals(updatedRecord.getAnimalId())
                && repository.existsByAnimalId(updatedRecord.getAnimalId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet animal possede deja un dossier medical"
            );
        }

        existing.setAnimalId(updatedRecord.getAnimalId());
        existing.setAllergies(updatedRecord.getAllergies());
        existing.setMedicalHistory(updatedRecord.getMedicalHistory());
        existing.setNotes(updatedRecord.getNotes());

        return repository.save(existing);
    }

    // DELETE
    public void delete(Long id) {
        MedicalRecord existing = findById(id);
        repository.delete(existing);
    }
}
