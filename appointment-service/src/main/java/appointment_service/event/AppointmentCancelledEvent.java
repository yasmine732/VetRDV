package appointment_service.event;

/**
 * Événement métier "AppointmentCancelled" du cahier des charges.
 * Publié dans l'application quand un rendez-vous passe à CANCELLED.
 */
public record AppointmentCancelledEvent(
        Long appointmentId,
        Long ownerId,
        Long animalId,
        Long vetId,
        Long slotId
) {
}
