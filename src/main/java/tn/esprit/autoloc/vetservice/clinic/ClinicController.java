package tn.esprit.autoloc.vetservice.clinic;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/clinics")
public class ClinicController {
    private final ClinicRepository repository;
    public ClinicController(ClinicRepository repository) { this.repository = repository; }
    @GetMapping public List<Clinic> all() { return repository.findAll(); }
    @GetMapping("/{id}") public Clinic one(@PathVariable Long id) { return repository.findById(id).orElseThrow(() -> new RuntimeException("Clinic introuvable")); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Clinic create(@Valid @RequestBody Clinic clinic) { clinic.setId(null); return repository.save(clinic); }
    @PutMapping("/{id}") public Clinic update(@PathVariable Long id, @Valid @RequestBody Clinic input) { Clinic c = one(id); c.setNom(input.getNom()); c.setAdresse(input.getAdresse()); c.setVille(input.getVille()); return repository.save(c); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { repository.delete(one(id)); }
}
