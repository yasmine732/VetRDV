package appointment_service.controller;

import appointment_service.dto.AppointmentRequest;
import appointment_service.dto.AppointmentResponse;
import appointment_service.dto.ErrorResponse;
import appointment_service.dto.StatusUpdateRequest;
import appointment_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST du contrat d'API Appointment Service.
 * Le controller reçoit la requête, valide les entrées et délègue au service : aucune logique métier ici.
 */
@RestController
@RequestMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointments", description = "Prise, consultation, annulation et suivi du statut des rendez-vous")
public class AppointmentController {

    private static final String EXAMPLE_APPOINTMENT = """
            {"id": 1, "ownerId": 1, "animalId": 3, "vetId": 2, "slotId": 10,
             "motif": "Vaccination annuelle", "statut": "PENDING"}""";

    private static final String EXAMPLE_404 = """
            {"timestamp": "2026-11-28T10:15:30", "status": 404, "error": "Not Found",
             "message": "Rendez-vous introuvable avec l'id 99", "path": "/appointments/99", "details": null}""";

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // ---------- A. Créer un rendez-vous ----------
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un rendez-vous",
            description = "Crée un rendez-vous avec le statut initial PENDING. Refuse un créneau déjà réservé.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            schema = @Schema(implementation = AppointmentRequest.class),
            examples = @ExampleObject(name = "Nouveau rendez-vous", value = """
                    {"ownerId": 1, "animalId": 3, "vetId": 2, "slotId": 10, "motif": "Vaccination annuelle"}""")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rendez-vous créé",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_APPOINTMENT))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp": "2026-11-28T10:15:30", "status": 400, "error": "Bad Request",
                                     "message": "Données invalides", "path": "/appointments",
                                     "details": {"vetId": "vetId est obligatoire", "motif": "motif est obligatoire"}}"""))),
            @ApiResponse(responseCode = "409", description = "Créneau déjà réservé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp": "2026-11-28T10:15:30", "status": 409, "error": "Conflict",
                                     "message": "Le créneau 10 est déjà réservé", "path": "/appointments", "details": null}""")))
    })
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse created = appointmentService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // ---------- B. Consulter un rendez-vous ----------
    @GetMapping("/{id}")
    @Operation(summary = "Consulter un rendez-vous par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rendez-vous trouvé",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_APPOINTMENT))),
            @ApiResponse(responseCode = "400", description = "Id invalide (non numérique ou négatif)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rendez-vous introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_404)))
    })
    public AppointmentResponse getById(
            @Parameter(description = "Identifiant du rendez-vous", example = "1")
            @PathVariable @Positive(message = "id doit être un nombre positif") Long id) {
        return appointmentService.getById(id);
    }

    // ---------- C. Consulter les rendez-vous d'un vétérinaire ----------
    @GetMapping
    @Operation(summary = "Lister les rendez-vous d'un vétérinaire",
            description = "Renvoie tous les rendez-vous du vétérinaire (liste vide s'il n'en a aucun).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)),
                            examples = @ExampleObject(value = "[" + EXAMPLE_APPOINTMENT + "]"))),
            @ApiResponse(responseCode = "400", description = "vetId absent ou invalide",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<AppointmentResponse> getByVetId(
            @Parameter(description = "Identifiant du vétérinaire", example = "2", required = true)
            @RequestParam @Positive(message = "vetId doit être un nombre positif") Long vetId) {
        return appointmentService.getByVetId(vetId);
    }

    // ---------- D. Annuler un rendez-vous ----------
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler un rendez-vous",
            description = "Passe le statut à CANCELLED sans supprimer la ligne en base. "
                    + "Le créneau redevient disponible pour une nouvelle réservation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rendez-vous annulé",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id": 1, "ownerId": 1, "animalId": 3, "vetId": 2, "slotId": 10,
                                     "motif": "Vaccination annuelle", "statut": "CANCELLED"}"""))),
            @ApiResponse(responseCode = "404", description = "Rendez-vous introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_404))),
            @ApiResponse(responseCode = "409", description = "Déjà annulé ou déjà terminé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp": "2026-11-28T10:15:30", "status": 409, "error": "Conflict",
                                     "message": "Le rendez-vous 1 est déjà annulé", "path": "/appointments/1/cancel", "details": null}""")))
    })
    public AppointmentResponse cancel(
            @Parameter(description = "Identifiant du rendez-vous", example = "1")
            @PathVariable @Positive(message = "id doit être un nombre positif") Long id) {
        return appointmentService.cancel(id);
    }

    // ---------- E. Modifier le statut ----------
    @PutMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifier le statut d'un rendez-vous",
            description = "Transitions autorisées : PENDING → CONFIRMED | CANCELLED, CONFIRMED → DONE | CANCELLED. "
                    + "CANCELLED et DONE sont finaux. Si l'en-tête X-User-Role vaut OWNER, seule l'annulation est permise.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            schema = @Schema(implementation = StatusUpdateRequest.class),
            examples = {
                    @ExampleObject(name = "Confirmer", value = "{\"statut\": \"CONFIRMED\"}"),
                    @ExampleObject(name = "Terminer", value = "{\"statut\": \"DONE\"}")
            }))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut modifié",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class),
                            examples = @ExampleObject(value = """
                                    {"id": 1, "ownerId": 1, "animalId": 3, "vetId": 2, "slotId": 10,
                                     "motif": "Vaccination annuelle", "statut": "CONFIRMED"}"""))),
            @ApiResponse(responseCode = "400", description = "Statut absent ou inconnu",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Rôle non autorisé pour ce changement",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp": "2026-11-28T10:15:30", "status": 403, "error": "Forbidden",
                                     "message": "Un propriétaire (OWNER) peut seulement annuler un rendez-vous",
                                     "path": "/appointments/1/status", "details": null}"""))),
            @ApiResponse(responseCode = "404", description = "Rendez-vous introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_404))),
            @ApiResponse(responseCode = "409", description = "Transition de statut interdite",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {"timestamp": "2026-11-28T10:15:30", "status": 409, "error": "Conflict",
                                     "message": "Impossible de modifier le rendez-vous 1 : le statut CANCELLED est final",
                                     "path": "/appointments/1/status", "details": null}""")))
    })
    public AppointmentResponse updateStatus(
            @Parameter(description = "Identifiant du rendez-vous", example = "1")
            @PathVariable @Positive(message = "id doit être un nombre positif") Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @Parameter(in = ParameterIn.HEADER, description = "Rôle de l'appelant, transmis plus tard par l'API Gateway "
                    + "(OWNER, VET ou ADMIN). Optionnel pour l'instant.", example = "VET")
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return appointmentService.updateStatus(id, request.statut(), role);
    }
}
