package tn.esprit.autoloc.vetservice.slot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.vetservice.veterinarian.Veterinarian;
import java.time.LocalDateTime;

@Entity
@Table(name = "slots", uniqueConstraints = @UniqueConstraint(columnNames = {"veterinarian_id", "debut", "fin"}))
public class Slot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotNull private LocalDateTime debut;
    @NotNull private LocalDateTime fin;
    private String statut = "LIBRE";
    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "veterinarian_id", nullable = false) private Veterinarian veterinarian;

    public Slot() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }
    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Veterinarian getVeterinarian() { return veterinarian; }
    public void setVeterinarian(Veterinarian veterinarian) { this.veterinarian = veterinarian; }
}
