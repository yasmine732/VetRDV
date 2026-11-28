package appointment_service.entity;

import appointment_service.enums.Statut;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Entité JPA : un rendez-vous enregistré dans la table "appointments" de appointment_db.
 * ownerId, animalId, vetId et slotId sont des identifiants gérés par les autres
 * microservices (User, Animal, Vet) : on stocke seulement leur identifiant, sans clé étrangère.
 */
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_vet", columnList = "vetId"),
        @Index(name = "idx_appointment_slot", columnList = "slotId")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long animalId;

    @Column(nullable = false)
    private Long vetId;

    @Column(nullable = false)
    private Long slotId;

    @Column(nullable = false, length = 255)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Statut statut;

    public Appointment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public Long getVetId() {
        return vetId;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }
}
