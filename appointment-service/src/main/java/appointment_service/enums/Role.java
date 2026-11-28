package appointment_service.enums;

/**
 * Rôles de VetRDV (mêmes valeurs que le User Service).
 * Plus tard, l'API Gateway vérifiera le JWT et transmettra le rôle dans l'en-tête X-User-Role.
 */
public enum Role {
    OWNER,
    VET,
    ADMIN
}
