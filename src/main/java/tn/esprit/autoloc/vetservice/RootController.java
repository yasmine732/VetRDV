package tn.esprit.autoloc.vetservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class RootController {
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "service", "vetservice",
                "status", "UP",
                "message", "VetRDV Vet Service fonctionne correctement",
                "endpoints", Map.of("clinics", "/clinics", "veterinarians", "/vets", "slots", "/slots")
        );
    }
}
