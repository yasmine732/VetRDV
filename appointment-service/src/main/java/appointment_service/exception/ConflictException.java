package appointment_service.exception;

/**
 * Levée quand l'opération entre en conflit avec l'état actuel des données : traduite en HTTP 409.
 * Exemples : créneau déjà réservé, rendez-vous déjà annulé, transition de statut interdite.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
