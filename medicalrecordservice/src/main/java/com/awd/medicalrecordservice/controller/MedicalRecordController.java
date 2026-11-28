
package com.awd.medicalrecordservice.controller;

import com.awd.medicalrecordservice.entity.MedicalRecord;
import com.awd.medicalrecordservice.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<MedicalRecord> create(
            @RequestBody MedicalRecord medicalRecord) {

        MedicalRecord created = service.create(medicalRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL
    @GetMapping
    public List<MedicalRecord> findAll() {
        return service.findAll();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public MedicalRecord findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // READ BY ANIMAL ID
    @GetMapping("/animal/{animalId}")
    public MedicalRecord findByAnimalId(@PathVariable Long animalId) {
        return service.findByAnimalId(animalId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public MedicalRecord update(
            @PathVariable Long id,
            @RequestBody MedicalRecord medicalRecord) {

        return service.update(id, medicalRecord);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
