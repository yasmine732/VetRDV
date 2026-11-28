package appointment_service.dto;

import appointment_service.entity.Appointment;
import appointment_service.enums.Statut;
import io.swagger.v3.oas.annotations.media.Schema;

/** Représentation d'un rendez-vous renvoyée par l'API (on n'expose pas directement l'entité JPA). */
@Schema(description = "Rendez-vous renvoyé par l'API")
public record AppointmentResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long ownerId,
        @Schema(example = "3") Long animalId,
        @Schema(example = "2") Long vetId,
        @Schema(example = "10") Long slotId,
        @Schema(example = "Vaccination annuelle") String motif,
        @Schema(example = "PENDING") Statut statut
) {

    public static AppointmentResponse fromEntity(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getOwnerId(),
                appointment.getAnimalId(),
                appointment.getVetId(),
                appointment.getSlotId(),
                appointment.getMotif(),
                appointment.getStatut()
        );
    }
}
