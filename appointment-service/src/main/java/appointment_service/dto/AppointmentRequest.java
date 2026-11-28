package appointment_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Corps de la requête POST /appointments.
 * Le client n'envoie ni l'id (généré par MySQL) ni le statut (PENDING par défaut).
 */
@Schema(description = "Données nécessaires pour créer un rendez-vous")
public record AppointmentRequest(

        @Schema(description = "Identifiant du propriétaire (User Service)", example = "1")
        @NotNull(message = "ownerId est obligatoire")
        @Positive(message = "ownerId doit être un nombre positif")
        Long ownerId,

        @Schema(description = "Identifiant de l'animal (Animal Service)", example = "3")
        @NotNull(message = "animalId est obligatoire")
        @Positive(message = "animalId doit être un nombre positif")
        Long animalId,

        @Schema(description = "Identifiant du vétérinaire (Vet Service)", example = "2")
        @NotNull(message = "vetId est obligatoire")
        @Positive(message = "vetId doit être un nombre positif")
        Long vetId,

        @Schema(description = "Identifiant du créneau (Vet Service)", example = "10")
        @NotNull(message = "slotId est obligatoire")
        @Positive(message = "slotId doit être un nombre positif")
        Long slotId,

        @Schema(description = "Motif de la consultation", example = "Vaccination annuelle")
        @NotBlank(message = "motif est obligatoire")
        @Size(max = 255, message = "motif ne doit pas dépasser 255 caractères")
        String motif
) {
}
