package appointment_service.repository;

import appointment_service.entity.Appointment;
import appointment_service.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Accès aux données avec Spring Data JPA.
 * Les requêtes SQL sont générées automatiquement à partir du nom des méthodes.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** Rendez-vous d'un vétérinaire, triés par id. */
    List<Appointment> findByVetIdOrderByIdAsc(Long vetId);

    /** Vrai si le créneau est déjà pris par un rendez-vous qui n'est pas dans l'état donné (ex. CANCELLED). */
    boolean existsBySlotIdAndStatutNot(Long slotId, Statut statut);
}
