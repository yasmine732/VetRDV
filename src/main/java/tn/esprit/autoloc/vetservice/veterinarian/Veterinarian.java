package tn.esprit.autoloc.vetservice.veterinarian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.vetservice.clinic.Clinic;
import tn.esprit.autoloc.vetservice.slot.Slot;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarians")
public class Veterinarian {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotNull private Long userId;
    @NotBlank private String nom;
    @NotBlank private String specialite;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "clinic_id") private Clinic clinic;
    @JsonIgnore @OneToMany(mappedBy = "veterinarian") private List<Slot> slots = new ArrayList<>();

    public Veterinarian() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public Clinic getClinic() { return clinic; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }
}
