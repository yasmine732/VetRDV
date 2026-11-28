# VetRDV — Appointment Service

Microservice de gestion des rendez-vous de la plateforme VetRDV (Spring Boot 4.2, Java 17, MySQL `appointment_db`, port **8083**).

## Lancer le service

1. Démarrer MySQL (XAMPP / WAMP / service MySQL). La base `appointment_db` est créée automatiquement (`createDatabaseIfNotExist=true`).
2. Dans IntelliJ : **Maven → Reload project** (pour télécharger springdoc), puis lancer `AppointmentServiceApplication`.
   En ligne de commande : `mvnw.cmd spring-boot:run` (Windows) ou `./mvnw spring-boot:run`.
3. Vérifier :
   - Swagger UI : http://localhost:8083/swagger-ui.html
   - Documentation JSON : http://localhost:8083/v3/api-docs
   - Table créée : dans MySQL, `USE appointment_db; DESCRIBE appointments;`

Compiler sans lancer les tests : `mvnw.cmd clean package -DskipTests`.
Le test `contextLoads` démarre toute l'application : il a besoin que MySQL soit lancé.

## Contrat d'API

| Endpoint | Rôle | Codes |
|---|---|---|
| `POST /appointments` | Créer un rendez-vous (statut initial `PENDING`) | 201 · 400 · 409 |
| `GET /appointments/{id}` | Consulter un rendez-vous | 200 · 400 · 404 |
| `GET /appointments?vetId={vetId}` | Rendez-vous d'un vétérinaire | 200 · 400 |
| `PUT /appointments/{id}/cancel` | Annulation logique (`CANCELLED`, la ligne reste en base) | 200 · 404 · 409 |
| `PUT /appointments/{id}/status` | Modifier le statut | 200 · 400 · 403 · 404 · 409 |

### Règles métier

- **Double réservation (409)** : un créneau (`slotId`) ne peut avoir qu'un seul rendez-vous non annulé. Après annulation, le créneau redevient libre.
- **Cycle de vie du statut** :
  `PENDING → CONFIRMED | CANCELLED`, `CONFIRMED → DONE | CANCELLED`. `CANCELLED` et `DONE` sont finaux. Toute autre transition donne **409**.
- **Annulation (409)** si le rendez-vous est déjà `CANCELLED` ou déjà `DONE`.
- **Rôles (403)** : l'en-tête optionnel `X-User-Role` (`OWNER`, `VET`, `ADMIN`) prépare l'arrivée de l'API Gateway. Un `OWNER` peut seulement passer le statut à `CANCELLED` ; un rôle inconnu est refusé. Sans en-tête, aucun contrôle (il n'y a pas encore de Gateway).

### Format des erreurs

```json
{
  "timestamp": "2026-11-28T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Données invalides",
  "path": "/appointments",
  "details": { "vetId": "vetId est obligatoire", "motif": "motif est obligatoire" }
}
```

## Tests Postman

Importer `postman/VetRDV-Appointment-Service.postman_collection.json` (**Import** dans Postman), puis **Run collection** dans l'ordre. Les variables `baseUrl` (`http://localhost:8083`) et `vetId` sont modifiables dans l'onglet *Variables* de la collection. Le `slotId` est tiré au hasard à chaque exécution pour pouvoir relancer la collection sans conflit.

- Dossier **CRUD** : création → consultation par id → par vétérinaire → `CONFIRMED` → annulation → vérification que la ligne existe toujours.
- Dossier **Erreurs** : id inexistant (404), données invalides (400), id non numérique (400), `vetId` manquant (400), statut inconnu (400), double annulation (409), transition interdite (409), créneau déjà réservé (409), `OWNER` qui confirme (403), re-réservation d'un créneau libéré (201).

Chaque requête contient des tests automatiques (onglet *Tests*) qui vérifient le code HTTP et le contenu de la réponse.

## Organisation du code

```
appointment_service
├── AppointmentServiceApplication   point d'entrée Spring Boot
├── entity/Appointment              entité JPA → table appointments
├── enums/Statut                    PENDING, CONFIRMED, CANCELLED, DONE + transitions autorisées
├── enums/Role                      OWNER, VET, ADMIN (contrôle 403)
├── repository/AppointmentRepository  Spring Data JPA
├── dto/                            AppointmentRequest, StatusUpdateRequest, AppointmentResponse, ErrorResponse
├── service/AppointmentService      toute la logique métier
├── controller/AppointmentController  endpoints REST + documentation Swagger
├── exception/                      exceptions métier + GlobalExceptionHandler
├── event/                          AppointmentConfirmed / AppointmentCancelled + listener
└── config/OpenApiConfig            titre et description de Swagger
```

### Rôle de chaque classe (pour la soutenance)

- **Appointment** : l'entité JPA. `@Id @GeneratedValue(IDENTITY)` laisse MySQL générer l'id (AUTO_INCREMENT). `@Enumerated(EnumType.STRING)` enregistre le statut en texte (`"PENDING"`) plutôt qu'en numéro, ce qui reste lisible et ne casse pas si on réordonne l'énumération. `ownerId`, `animalId`, `vetId`, `slotId` sont de simples identifiants : chaque microservice a sa propre base, donc pas de clé étrangère entre services.
- **Statut** : l'énumération du cahier des charges, avec la méthode `peutPasserA()` qui centralise les transitions autorisées.
- **AppointmentRepository** : interface Spring Data JPA ; Spring génère l'implémentation. `findByVetIdOrderByIdAsc` et `existsBySlotIdAndStatutNot` sont traduites automatiquement en requêtes SQL à partir de leur nom.
- **DTO** : séparent ce que l'API reçoit/renvoie de l'entité. `AppointmentRequest` ne contient ni id ni statut, donc un client ne peut pas les imposer. Les annotations `@NotNull`, `@Positive`, `@NotBlank` déclenchent la validation (400).
- **AppointmentService** : la logique métier (statut initial, double réservation, transitions, rôles, publication des événements). `@Transactional` garantit qu'une opération est entièrement enregistrée ou pas du tout.
- **AppointmentController** : reçoit les requêtes HTTP, valide avec `@Valid`, appelle le service et renvoie le bon code (201 + en-tête `Location` à la création). Aucune règle métier dedans.
- **GlobalExceptionHandler** (`@RestControllerAdvice`) : transforme chaque exception en réponse JSON uniforme avec le bon code HTTP (400, 403, 404, 409, 500).
- **Événements** : `AppointmentConfirmedEvent` et `AppointmentCancelledEvent` sont publiés quand le statut passe à `CONFIRMED` ou `CANCELLED`. `AppointmentEventListener` les reçoit après validation de la transaction ; pour l'instant il les journalise.
- **OpenApiConfig** : informations affichées en tête de Swagger UI.

Toutes les dépendances sont injectées **par constructeur** (pas de `@Autowired` sur les champs).

## Intégrations prévues

- **Animal Service / Vet Service** : l'emplacement est marqué dans `AppointmentService.create()`. Il suffira d'y appeler `GET /animals/{id}` et `GET /vets/{id}/slots` (RestClient ou OpenFeign + Eureka) avant l'enregistrement.
- **RabbitMQ** : remplacer le contenu de `AppointmentEventListener` par l'envoi du message vers le broker. Le service et le controller n'auront pas à changer.
- **API Gateway + JWT** : la Gateway transmettra le rôle dans `X-User-Role`, déjà lu par l'endpoint `/status`.
- **Eureka** : ajouter `spring-cloud-starter-netflix-eureka-client` et `eureka.client.service-url.defaultZone` dans `application.properties`. Attention : le port **8083** de ce service est le même que celui du service Meeting dans l'atelier Eureka ; si vous lancez les deux en même temps, changez l'un des deux ports.
