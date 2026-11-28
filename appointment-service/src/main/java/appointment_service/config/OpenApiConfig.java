package appointment_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Informations générales affichées en haut de Swagger UI.
 * Swagger UI : http://localhost:{port}/swagger-ui.html — JSON OpenAPI : /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appointmentOpenApi(@Value("${server.port:8080}") String port) {
        return new OpenAPI()
                .info(new Info()
                        .title("VetRDV — Appointment Service API")
                        .version("1.0.0")
                        .description("Microservice de gestion des rendez-vous vétérinaires : création, consultation, "
                                + "annulation logique et cycle de vie du statut (PENDING, CONFIRMED, CANCELLED, DONE).")
                        .contact(new Contact().name("Équipe Women in Tech — VetRDV")))
                .servers(List.of(new Server().url("http://localhost:" + port).description("Local")));
    }
}
