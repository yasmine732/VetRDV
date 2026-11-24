# VetRDV - Vet Service

Microservice Spring Boot pour gerer les cliniques, les veterinaires et leurs creneaux.

## Demarrage

1. Importer `database/vet_db_mysql.sql` dans phpMyAdmin.
2. Demarrer Eureka depuis le dossier `eureka-server`.
3. Demarrer ce service avec `mvnw.cmd spring-boot:run`.

Le service utilise le port `8083`, la base MySQL `vet_db` et s'enregistre dans Eureka sous le nom `VETSERVICE`.

## URLs

- API : http://localhost:8083
- Swagger : http://localhost:8083/swagger-ui.html
- OpenAPI : http://localhost:8083/v3/api-docs
- Eureka : http://localhost:8761

## Endpoints

- `GET|POST|PUT|DELETE /clinics`
- `GET|POST|PUT|DELETE /vets`
- `GET /vets?specialite=Chirurgie`
- `GET|POST|PUT|DELETE /slots`
- `GET /slots/vet/{vetId}`

## Exemple de donnees

Creation d'un veterinaire :

```json
{
  "userId": 10,
  "nom": "Dr. Amel Ben Ali",
  "specialite": "Chirurgie",
  "clinicId": 1
}
```

Creation d'un creneau :

```json
{
  "debut": "2026-12-01T09:00:00",
  "fin": "2026-12-01T09:30:00",
  "statut": "LIBRE",
  "vetId": 1
}
```

Le projet utilise MySQL pour faciliter les tests avec phpMyAdmin. La presentation initiale prevoyait PostgreSQL pour Vet Service.
