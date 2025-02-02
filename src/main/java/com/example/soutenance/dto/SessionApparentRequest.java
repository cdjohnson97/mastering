package com.example.soutenance.dto;

import com.example.soutenance.model.StatutInscription;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionApparentRequest {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private StatutInscription statut;
}
