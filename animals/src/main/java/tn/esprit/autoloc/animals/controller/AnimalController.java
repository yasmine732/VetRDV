
package tn.esprit.autoloc.animals.controller;

import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.animals.entity.Animal;
import tn.esprit.autoloc.animals.service.AnimalService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    // Ajouter un animal
    @PostMapping
    public Animal ajouterAnimal(@RequestBody Animal animal) {
        return animalService.ajouterAnimal(animal);
    }

    // Afficher tous les animaux
    @GetMapping
    public List<Animal> afficherAnimaux() {
        return animalService.afficherAnimaux();
    }

    // Afficher un animal par ID
    @GetMapping("/{id}")
    public Optional<Animal> afficherAnimal(@PathVariable Long id) {
        return animalService.afficherAnimal(id);
    }

    // Modifier un animal
    @PutMapping("/{id}")
    public Animal modifierAnimal(
            @PathVariable Long id,
            @RequestBody Animal animal) {
        return animalService.modifierAnimal(id, animal);
    }

    // Supprimer un animal
    @DeleteMapping("/{id}")
    public void supprimerAnimal(@PathVariable Long id) {
        animalService.supprimerAnimal(id);
    }
}
