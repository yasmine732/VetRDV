package appointment_service.exception;

/** Levée quand un rendez-vous n'existe pas : traduite en HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
