package appointment_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/** Format unique des réponses d'erreur (400, 403, 404, 409, 500). */
@Schema(description = "Réponse d'erreur")
public record ErrorResponse(
        @Schema(example = "2026-11-28T10:15:30") LocalDateTime timestamp,
        @Schema(example = "404") int status,
        @Schema(example = "Not Found") String error,
        @Schema(example = "Rendez-vous introuvable avec l'id 99") String message,
        @Schema(example = "/appointments/99") String path,
        @Schema(description = "Erreurs par champ (uniquement pour les erreurs de validation)")
        Map<String, String> details
) {
}
