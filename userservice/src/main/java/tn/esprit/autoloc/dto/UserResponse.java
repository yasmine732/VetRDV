package tn.esprit.autoloc.dto;

import tn.esprit.autoloc.entity.Role;

public class UserResponse {

    private Long id;
    private String email;
    private Role role;
    private String statut;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, Role role, String statut) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}