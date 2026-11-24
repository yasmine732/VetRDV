package tn.esprit.autoloc.animals.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.animals.entity.Animal;
import tn.esprit.autoloc.animals.repository.AnimalRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    // Ajouter un animal
    public Animal ajouterAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    // Afficher tous les animaux
    public List<Animal> afficherAnimaux() {
        return animalRepository.findAll();
    }

    // Afficher un animal par ID
    public Optional<Animal> afficherAnimal(Long id) {
        return animalRepository.findById(id);
    }

    // Modifier un animal
    public Animal modifierAnimal(Long id, Animal nouvelAnimal) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal introuvable"));

        animal.setNom(nouvelAnimal.getNom());
        animal.setEspece(nouvelAnimal.getEspece());
        animal.setRace(nouvelAnimal.getRace());
        animal.setDateNaissance(nouvelAnimal.getDateNaissance());
        animal.setOwnerId(nouvelAnimal.getOwnerId());

        return animalRepository.save(animal);
    }

    // Supprimer un animal
    public void supprimerAnimal(Long id) {
        animalRepository.deleteById(id);
    }
}