package appointment_service.exception;

/** Levée quand le rôle de l'appelant n'autorise pas l'opération : traduite en HTTP 403. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
