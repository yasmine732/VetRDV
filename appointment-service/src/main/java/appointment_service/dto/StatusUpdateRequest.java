package appointment_service.dto;

import appointment_service.enums.Statut;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Corps de la requête PUT /appointments/{id}/status. */
@Schema(description = "Nouveau statut du rendez-vous")
public record StatusUpdateRequest(

        @Schema(description = "Nouveau statut", example = "CONFIRMED",
                allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "DONE"})
        @NotNull(message = "statut est obligatoire")
        Statut statut
) {
}
