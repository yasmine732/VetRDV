package tn.esprit.autoloc.vetservice.veterinarian;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.vetservice.clinic.ClinicRepository;
import java.util.List;

@RestController
@RequestMapping("/vets")
public class VeterinarianController {
    private final VeterinarianRepository vets;
    private final ClinicRepository clinics;

    public VeterinarianController(VeterinarianRepository vets, ClinicRepository clinics) { this.vets = vets; this.clinics = clinics; }
    @GetMapping public List<Veterinarian> all(@RequestParam(required = false) String specialite) { return specialite == null ? vets.findAll() : vets.findBySpecialiteIgnoreCase(specialite); }
    @GetMapping("/{id}") public Veterinarian one(@PathVariable Long id) { return vets.findById(id).orElseThrow(() -> new RuntimeException("Veterinaire introuvable")); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Veterinarian create(@Valid @RequestBody VetRequest request) { Veterinarian vet = new Veterinarian(); copy(vet, request); return vets.save(vet); }
    @PutMapping("/{id}") public Veterinarian update(@PathVariable Long id, @Valid @RequestBody VetRequest request) { Veterinarian vet = one(id); copy(vet, request); return vets.save(vet); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { vets.delete(one(id)); }
    private void copy(Veterinarian vet, VetRequest request) { vet.setUserId(request.userId()); vet.setNom(request.nom()); vet.setSpecialite(request.specialite()); vet.setClinic(clinics.findById(request.clinicId()).orElseThrow(() -> new RuntimeException("Clinic introuvable"))); }

    public record VetRequest(@jakarta.validation.constraints.NotNull Long userId, @jakarta.validation.constraints.NotBlank String nom, @jakarta.validation.constraints.NotBlank String specialite, @jakarta.validation.constraints.NotNull Long clinicId) {}
}
