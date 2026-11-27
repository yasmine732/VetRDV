
package tn.esprit.autoloc.service;

import tn.esprit.autoloc.entity.User;
import tn.esprit.autoloc.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Ajouter un utilisateur
    public User ajouterUser(User user) {
        return userRepository.save(user);
    }

    // Afficher tous les utilisateurs
    public List<User> afficherUsers() {
        return userRepository.findAll();
    }

    // Afficher un utilisateur par ID
    public User afficherUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    // Modifier un utilisateur
    public User modifierUser(Long id, User userDetails) {
        User user = afficherUser(id);

        user.setEmail(userDetails.getEmail());
        user.setMotDePasse(userDetails.getMotDePasse());
        user.setRole(userDetails.getRole());
        user.setStatut(userDetails.getStatut());

        return userRepository.save(user);
    }

    // Supprimer un utilisateur
    public void supprimerUser(Long id) {
        User user = afficherUser(id);
        userRepository.delete(user);
    }

    // Login : vérifier email et mot de passe
    public User login(String email, String motDePasse) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email incorrect"));

        if (!user.getMotDePasse().equals(motDePasse)) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        return user;
    }
}
