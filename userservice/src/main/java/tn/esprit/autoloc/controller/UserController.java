
package tn.esprit.autoloc.controller;

import tn.esprit.autoloc.dto.UserRequest;
import tn.esprit.autoloc.dto.UserResponse;
import tn.esprit.autoloc.dto.LoginRequest;
import tn.esprit.autoloc.entity.User;
import tn.esprit.autoloc.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Convertir User en UserResponse
    private UserResponse convertirEnResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatut()
        );
    }

    // Ajouter un utilisateur
    @PostMapping
    public UserResponse ajouterUser(@RequestBody UserRequest request) {
        User user = new User();

        user.setEmail(request.getEmail());
        user.setMotDePasse(request.getMotDePasse());
        user.setRole(request.getRole());
        user.setStatut(request.getStatut());

        return convertirEnResponse(userService.ajouterUser(user));
    }

    // Afficher tous les utilisateurs
    @GetMapping
    public List<UserResponse> afficherUsers() {
        return userService.afficherUsers()
                .stream()
                .map(this::convertirEnResponse)
                .toList();
    }

    // Afficher un utilisateur par ID
    @GetMapping("/{id}")
    public UserResponse afficherUser(@PathVariable Long id) {
        return convertirEnResponse(userService.afficherUser(id));
    }

    // Modifier un utilisateur
    @PutMapping("/{id}")
    public UserResponse modifierUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setMotDePasse(request.getMotDePasse());
        user.setRole(request.getRole());
        user.setStatut(request.getStatut());

        return convertirEnResponse(userService.modifierUser(id, user));
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public String supprimerUser(@PathVariable Long id) {
        userService.supprimerUser(id);
        return "Utilisateur supprimé avec succès";
    }

    // Login
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) {

        User user = userService.login(
                request.getEmail(),
                request.getMotDePasse()
        );

        return convertirEnResponse(user);
    }
}
