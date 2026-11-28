package appointment_service.event;

/**
 * Événement métier "AppointmentConfirmed" du cahier des charges.
 * Publié dans l'application quand un rendez-vous passe à CONFIRMED.
 */
public record AppointmentConfirmedEvent(
        Long appointmentId,
        Long ownerId,
        Long animalId,
        Long vetId,
        Long slotId
) {
}
