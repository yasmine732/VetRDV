package tn.esprit.autoloc.vetservice.slot;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.vetservice.veterinarian.VeterinarianRepository;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/slots")
public class SlotController {
    private final SlotRepository slots;
    private final VeterinarianRepository vets;

    public SlotController(SlotRepository slots, VeterinarianRepository vets) { this.slots = slots; this.vets = vets; }
    @GetMapping public List<Slot> all() { return slots.findAll(); }
    @GetMapping("/{id}") public Slot one(@PathVariable Long id) { return slots.findById(id).orElseThrow(() -> new RuntimeException("Creneau introuvable")); }
    @GetMapping("/vet/{vetId}") public List<Slot> byVet(@PathVariable Long vetId) { vets.findById(vetId).orElseThrow(() -> new RuntimeException("Veterinaire introuvable")); return slots.findByVeterinarianId(vetId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Slot create(@Valid @RequestBody SlotRequest request) { Slot slot = new Slot(); copy(slot, request); return slots.save(slot); }
    @PutMapping("/{id}") public Slot update(@PathVariable Long id, @Valid @RequestBody SlotRequest request) { Slot slot = one(id); copy(slot, request); return slots.save(slot); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { slots.delete(one(id)); }
    private void copy(Slot slot, SlotRequest request) { if (!request.fin().isAfter(request.debut())) throw new IllegalArgumentException("La fin doit etre apres le debut"); slot.setDebut(request.debut()); slot.setFin(request.fin()); slot.setStatut(request.statut() == null ? "LIBRE" : request.statut()); slot.setVeterinarian(vets.findById(request.vetId()).orElseThrow(() -> new RuntimeException("Veterinaire introuvable"))); }

    public record SlotRequest(@jakarta.validation.constraints.NotNull LocalDateTime debut, @jakarta.validation.constraints.NotNull LocalDateTime fin, String statut, @jakarta.validation.constraints.NotNull Long vetId) {}
}
