package tn.esprit.autoloc.vetservice.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByVeterinarianId(Long veterinarianId);
}
