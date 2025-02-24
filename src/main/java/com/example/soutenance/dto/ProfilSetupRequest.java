package com.example.soutenance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfilSetupRequest {
    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    private String email;

    private String telephone;
    private String niveauEtude;
    private String specialite;
}
