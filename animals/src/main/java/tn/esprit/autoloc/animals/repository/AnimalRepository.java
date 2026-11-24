package tn.esprit.autoloc.animals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.animals.entity.Animal;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

}