package appointment_service.enums;

/**
 * Cycle de vie d'un rendez-vous.
 *
 * PENDING   -> CONFIRMED | CANCELLED
 * CONFIRMED -> DONE | CANCELLED
 * CANCELLED et DONE sont des états finaux.
 */
public enum Statut {
    PENDING,
    CONFIRMED,
    CANCELLED,
    DONE;

    /** Indique si le passage de l'état courant vers {@code cible} est autorisé. */
    public boolean peutPasserA(Statut cible) {
        return switch (this) {
            case PENDING -> cible == CONFIRMED || cible == CANCELLED;
            case CONFIRMED -> cible == DONE || cible == CANCELLED;
            case CANCELLED, DONE -> false;
        };
    }

    /** Un rendez-vous dans un état final ne peut plus changer. */
    public boolean estFinal() {
        return this == CANCELLED || this == DONE;
    }
}
