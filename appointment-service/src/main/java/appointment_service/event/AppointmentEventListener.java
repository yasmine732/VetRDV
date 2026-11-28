package appointment_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Reçoit les événements une fois la transaction validée en base (AFTER_COMMIT par défaut) :
 * on ne prévient jamais les autres services d'un changement qui aurait été annulé.
 *
 * Aujourd'hui l'événement est seulement journalisé. Quand RabbitMQ sera en place,
 * c'est ici qu'on enverra le message vers Notification Service et Vet Service.
 */
@Component
public class AppointmentEventListener {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventListener.class);

    @TransactionalEventListener
    public void onConfirmed(AppointmentConfirmedEvent event) {
        log.info("AppointmentConfirmed : {}", event);
    }

    @TransactionalEventListener
    public void onCancelled(AppointmentCancelledEvent event) {
        log.info("AppointmentCancelled : {}", event);
    }
}
