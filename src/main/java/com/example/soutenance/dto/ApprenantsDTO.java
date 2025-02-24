package com.example.soutenance.dto;

import com.example.soutenance.model.StatutInscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprenantsDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String niveauEtude;
    private String specialite;
    private boolean emailVerified;
    private StatutInscription status;
}