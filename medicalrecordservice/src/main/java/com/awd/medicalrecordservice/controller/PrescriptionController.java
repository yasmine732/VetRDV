
package com.awd.medicalrecordservice.controller;

import com.awd.medicalrecordservice.entity.Prescription;
import com.awd.medicalrecordservice.service.PrescriptionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    public record PrescriptionRequest(
            Long medicalRecordId,
            Long veterinarianId,
            Long consultationId,
            String medicationName,
            String dosage,
            String frequency,
            String duration,
            LocalDate prescriptionDate,
            String instructions
    ) {}

    public record PrescriptionResponse(
            Long id,
            Long medicalRecordId,
            Long veterinarianId,
            Long consultationId,
            String medicationName,
            String dosage,
            String frequency,
            String duration,
            LocalDate prescriptionDate,
            String instructions
    ) {}

    private Prescription toEntity(PrescriptionRequest request) {
        Prescription prescription = new Prescription();

        prescription.setVeterinarianId(request.veterinarianId());
        prescription.setConsultationId(request.consultationId());
        prescription.setMedicationName(request.medicationName());
        prescription.setDosage(request.dosage());
        prescription.setFrequency(request.frequency());
        prescription.setDuration(request.duration());
        prescription.setPrescriptionDate(request.prescriptionDate());
        prescription.setInstructions(request.instructions());

        return prescription;
    }

    private PrescriptionResponse toResponse(Prescription p) {
        return new PrescriptionResponse(
                p.getId(),
                p.getMedicalRecord().getId(),
                p.getVeterinarianId(),
                p.getConsultationId(),
                p.getMedicationName(),
                p.getDosage(),
                p.getFrequency(),
                p.getDuration(),
                p.getPrescriptionDate(),
                p.getInstructions()
        );
    }

    // CREATE
    @PostMapping
    public ResponseEntity<PrescriptionResponse> create(
            @RequestBody PrescriptionRequest request) {

        if (request.medicalRecordId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Prescription created = prescriptionService.create(
                request.medicalRecordId(),
                toEntity(request)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(created));
    }

    // READ ALL
    @GetMapping
    public List<PrescriptionResponse> findAll() {
        return prescriptionService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public PrescriptionResponse findById(@PathVariable Long id) {
        return toResponse(prescriptionService.findById(id));
    }

    // READ BY MEDICAL RECORD
    @GetMapping("/medical-record/{medicalRecordId}")
    public List<PrescriptionResponse> findByMedicalRecordId(
            @PathVariable Long medicalRecordId) {

        return prescriptionService
                .findByMedicalRecordId(medicalRecordId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ BY CONSULTATION
    @GetMapping("/consultation/{consultationId}")
    public List<PrescriptionResponse> findByConsultationId(
            @PathVariable Long consultationId) {

        return prescriptionService
                .findByConsultationId(consultationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    @PutMapping("/{id}")
    public PrescriptionResponse update(
            @PathVariable Long id,
            @RequestBody PrescriptionRequest request) {

        return toResponse(
                prescriptionService.update(id, toEntity(request))
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
