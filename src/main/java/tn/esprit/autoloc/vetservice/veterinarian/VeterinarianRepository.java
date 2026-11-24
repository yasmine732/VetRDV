package tn.esprit.autoloc.vetservice.veterinarian;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
    List<Veterinarian> findBySpecialiteIgnoreCase(String specialite);
}
