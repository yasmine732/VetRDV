
package com.awd.medicalrecordservice.controller;

import com.awd.medicalrecordservice.entity.Consultation;
import com.awd.medicalrecordservice.entity.MedicalRecord;
import com.awd.medicalrecordservice.service.ConsultationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(
            ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    // DTO : donnees envoyees depuis Swagger
    public record ConsultationRequest(
            Long medicalRecordId,
            Long veterinarianId,
            Long appointmentId,
            LocalDate consultationDate,
            String reason,
            String diagnosis,
            String treatment,
            String notes
    ) {}

    // DTO : donnees retournees par l'API
    public record ConsultationResponse(
            Long id,
            Long medicalRecordId,
            Long veterinarianId,
            Long appointmentId,
            LocalDate consultationDate,
            String reason,
            String diagnosis,
            String treatment,
            String notes
    ) {}

    private Consultation toEntity(ConsultationRequest request) {
        Consultation consultation = new Consultation();

        consultation.setVeterinarianId(request.veterinarianId());
        consultation.setAppointmentId(request.appointmentId());
        consultation.setConsultationDate(request.consultationDate());
        consultation.setReason(request.reason());
        consultation.setDiagnosis(request.diagnosis());
        consultation.setTreatment(request.treatment());
        consultation.setNotes(request.notes());

        return consultation;
    }

    private ConsultationResponse toResponse(Consultation c) {
        return new ConsultationResponse(
                c.getId(),
                c.getMedicalRecord().getId(),
                c.getVeterinarianId(),
                c.getAppointmentId(),
                c.getConsultationDate(),
                c.getReason(),
                c.getDiagnosis(),
                c.getTreatment(),
                c.getNotes()
        );
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ConsultationResponse> create(
            @RequestBody ConsultationRequest request) {

        if (request.medicalRecordId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Consultation created = consultationService.create(
                request.medicalRecordId(),
                toEntity(request)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(created));
    }

    // READ ALL
    @GetMapping
    public List<ConsultationResponse> findAll() {
        return consultationService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ConsultationResponse findById(@PathVariable Long id) {
        return toResponse(consultationService.findById(id));
    }

    // READ BY MEDICAL RECORD
    @GetMapping("/medical-record/{medicalRecordId}")
    public List<ConsultationResponse> findByMedicalRecordId(
            @PathVariable Long medicalRecordId) {

        return consultationService
                .findByMedicalRecordId(medicalRecordId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ BY VETERINARIAN
    @GetMapping("/veterinarian/{veterinarianId}")
    public List<ConsultationResponse> findByVeterinarianId(
            @PathVariable Long veterinarianId) {

        return consultationService
                .findByVeterinarianId(veterinarianId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ConsultationResponse update(
            @PathVariable Long id,
            @RequestBody ConsultationRequest request) {

        return toResponse(
                consultationService.update(id, toEntity(request))
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consultationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
