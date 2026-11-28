package appointment_service.service;

import appointment_service.dto.AppointmentRequest;
import appointment_service.dto.AppointmentResponse;
import appointment_service.entity.Appointment;
import appointment_service.enums.Role;
import appointment_service.enums.Statut;
import appointment_service.event.AppointmentCancelledEvent;
import appointment_service.event.AppointmentConfirmedEvent;
import appointment_service.exception.ConflictException;
import appointment_service.exception.ForbiddenException;
import appointment_service.exception.ResourceNotFoundException;
import appointment_service.repository.AppointmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Logique métier des rendez-vous : création, consultation, annulation et changement de statut.
 * Le controller ne fait qu'appeler ces méthodes.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Injection de dépendances par constructeur
    public AppointmentService(AppointmentRepository appointmentRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.appointmentRepository = appointmentRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Crée un rendez-vous avec le statut PENDING.
     * 409 si le créneau est déjà occupé par un rendez-vous non annulé.
     */
    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        // Intégration future : vérifier l'animal (GET /animals/{id} sur Animal Service)
        // et le créneau (GET /vets/{id}/slots sur Vet Service) avant d'enregistrer.

        if (appointmentRepository.existsBySlotIdAndStatutNot(request.slotId(), Statut.CANCELLED)) {
            throw new ConflictException("Le créneau " + request.slotId() + " est déjà réservé");
        }

        Appointment appointment = new Appointment();
        appointment.setOwnerId(request.ownerId());
        appointment.setAnimalId(request.animalId());
        appointment.setVetId(request.vetId());
        appointment.setSlotId(request.slotId());
        appointment.setMotif(request.motif().trim());
        appointment.setStatut(Statut.PENDING);

        return AppointmentResponse.fromEntity(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        return AppointmentResponse.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByVetId(Long vetId) {
        return appointmentRepository.findByVetIdOrderByIdAsc(vetId).stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    /**
     * Annulation logique : le statut passe à CANCELLED, la ligne reste en base.
     * 409 si le rendez-vous est déjà annulé ou déjà terminé.
     */
    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = findOrThrow(id);
        Statut actuel = appointment.getStatut();

        if (actuel == Statut.CANCELLED) {
            throw new ConflictException("Le rendez-vous " + id + " est déjà annulé");
        }
        if (actuel == Statut.DONE) {
            throw new ConflictException("Le rendez-vous " + id + " est déjà terminé et ne peut plus être annulé");
        }

        return applyStatus(appointment, Statut.CANCELLED);
    }

    /**
     * Change le statut en respectant le cycle de vie (voir {@link Statut#peutPasserA}).
     *
     * @param roleHeader valeur de l'en-tête X-User-Role (peut être null tant que l'API Gateway n'existe pas).
     *                   Un OWNER peut seulement annuler ; VET et ADMIN peuvent tout faire. Sinon 403.
     */
    @Transactional
    public AppointmentResponse updateStatus(Long id, Statut nouveauStatut, String roleHeader) {
        checkRoleCanChangeStatus(roleHeader, nouveauStatut);

        Appointment appointment = findOrThrow(id);
        Statut actuel = appointment.getStatut();

        if (!actuel.peutPasserA(nouveauStatut)) {
            String raison = actuel.estFinal()
                    ? "le statut " + actuel + " est final"
                    : "transition " + actuel + " -> " + nouveauStatut + " non autorisée";
            throw new ConflictException("Impossible de modifier le rendez-vous " + id + " : " + raison);
        }

        return applyStatus(appointment, nouveauStatut);
    }

    private AppointmentResponse applyStatus(Appointment appointment, Statut nouveauStatut) {
        appointment.setStatut(nouveauStatut);
        Appointment saved = appointmentRepository.save(appointment);

        if (nouveauStatut == Statut.CONFIRMED) {
            eventPublisher.publishEvent(new AppointmentConfirmedEvent(
                    saved.getId(), saved.getOwnerId(), saved.getAnimalId(), saved.getVetId(), saved.getSlotId()));
        } else if (nouveauStatut == Statut.CANCELLED) {
            eventPublisher.publishEvent(new AppointmentCancelledEvent(
                    saved.getId(), saved.getOwnerId(), saved.getAnimalId(), saved.getVetId(), saved.getSlotId()));
        }

        return AppointmentResponse.fromEntity(saved);
    }

    private void checkRoleCanChangeStatus(String roleHeader, Statut nouveauStatut) {
        if (roleHeader == null || roleHeader.isBlank()) {
            return; // pas encore de Gateway : aucun contrôle de rôle
        }

        Role role;
        try {
            role = Role.valueOf(roleHeader.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Rôle inconnu : " + roleHeader);
        }

        if (role == Role.OWNER && nouveauStatut != Statut.CANCELLED) {
            throw new ForbiddenException("Un propriétaire (OWNER) peut seulement annuler un rendez-vous");
        }
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable avec l'id " + id));
    }
}
