package appointment_service.exception;

import appointment_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestion centralisée des exceptions : chaque erreur est transformée en un JSON
 * {@link ErrorResponse} avec le bon code HTTP, au lieu d'une page d'erreur générique.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    /** Erreurs de validation du corps JSON (@Valid sur un DTO). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> details.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Données invalides", request, details);
    }

    /** Erreurs de validation sur les paramètres (@Positive sur un @PathVariable ou @RequestParam). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String name = result.getMethodParameter().getParameterName();
            result.getResolvableErrors()
                    .forEach(error -> details.putIfAbsent(name != null ? name : "parametre", error.getDefaultMessage()));
        });
        return build(HttpStatus.BAD_REQUEST, "Paramètres invalides", request, details);
    }

    /** JSON mal formé ou valeur d'énumération inconnue (ex. "statut": "FINISHED"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Corps de requête illisible : JSON mal formé ou valeur invalide (statuts acceptés : PENDING, CONFIRMED, CANCELLED, DONE)",
                request, null);
    }

    /** Paramètre de type incorrect (ex. /appointments/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Valeur invalide pour le paramètre '" + ex.getName() + "' : " + ex.getValue(), request, null);
    }

    /** Paramètre obligatoire absent (ex. GET /appointments sans vetId). */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Le paramètre '" + ex.getParameterName() + "' est obligatoire", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED,
                "Méthode " + ex.getMethod() + " non supportée sur cette URL", request, null);
    }

    /** URL inconnue (ex. /appointment au lieu de /appointments). */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Ressource introuvable : " + request.getRequestURI(), request, null);
    }

    /** Filet de sécurité : toute erreur imprévue devient un 500 propre (le détail reste dans les logs). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erreur inattendue sur {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur", request, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest request, Map<String, String> details) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
