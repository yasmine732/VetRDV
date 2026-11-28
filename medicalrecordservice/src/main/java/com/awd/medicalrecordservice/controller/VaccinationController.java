
package com.awd.medicalrecordservice.controller;

import com.awd.medicalrecordservice.entity.Vaccination;
import com.awd.medicalrecordservice.service.VaccinationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    public record VaccinationRequest(
            Long medicalRecordId,
            String vaccineName,
            LocalDate vaccinationDate,
            LocalDate nextDueDate,
            String batchNumber,
            Long veterinarianId,
            String notes
    ) {}

    public record VaccinationResponse(
            Long id,
            Long medicalRecordId,
            String vaccineName,
            LocalDate vaccinationDate,
            LocalDate nextDueDate,
            String batchNumber,
            Long veterinarianId,
            String notes
    ) {}

    private Vaccination toEntity(VaccinationRequest request) {
        Vaccination vaccination = new Vaccination();

        vaccination.setVaccineName(request.vaccineName());
        vaccination.setVaccinationDate(request.vaccinationDate());
        vaccination.setNextDueDate(request.nextDueDate());
        vaccination.setBatchNumber(request.batchNumber());
        vaccination.setVeterinarianId(request.veterinarianId());
        vaccination.setNotes(request.notes());

        return vaccination;
    }

    private VaccinationResponse toResponse(Vaccination v) {
        return new VaccinationResponse(
                v.getId(),
                v.getMedicalRecord().getId(),
                v.getVaccineName(),
                v.getVaccinationDate(),
                v.getNextDueDate(),
                v.getBatchNumber(),
                v.getVeterinarianId(),
                v.getNotes()
        );
    }

    // CREATE
    @PostMapping
    public ResponseEntity<VaccinationResponse> create(
            @RequestBody VaccinationRequest request) {

        if (request.medicalRecordId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Vaccination created = vaccinationService.create(
                request.medicalRecordId(),
                toEntity(request)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(created));
    }

    // READ ALL
    @GetMapping
    public List<VaccinationResponse> findAll() {
        return vaccinationService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public VaccinationResponse findById(@PathVariable Long id) {
        return toResponse(vaccinationService.findById(id));
    }

    // READ BY MEDICAL RECORD
    @GetMapping("/medical-record/{medicalRecordId}")
    public List<VaccinationResponse> findByMedicalRecord(
            @PathVariable Long medicalRecordId) {

        return vaccinationService
                .findByMedicalRecordId(medicalRecordId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    @PutMapping("/{id}")
    public VaccinationResponse update(
            @PathVariable Long id,
            @RequestBody VaccinationRequest request) {

        return toResponse(
                vaccinationService.update(id, toEntity(request))
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaccinationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
