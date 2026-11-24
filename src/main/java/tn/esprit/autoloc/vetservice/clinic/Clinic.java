package tn.esprit.autoloc.vetservice.clinic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import tn.esprit.autoloc.vetservice.veterinarian.Veterinarian;

@Entity
@Table(name = "clinics")
public class Clinic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank private String nom;
    @NotBlank private String adresse;
    @NotBlank private String ville;
    @JsonIgnore @OneToMany(mappedBy = "clinic") private List<Veterinarian> veterinarians = new ArrayList<>();

    public Clinic() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
}
