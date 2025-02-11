package com.example.soutenance.dto;


import lombok.Data;


import java.time.LocalDateTime;

@Data
public class InscriptionRequest {
    private Long soutenanceId; // Doit correspondre au nom
    private String emailEtudiant;
    private LocalDateTime creneauHoraire;

    // Getters/Setters obligatoires si pas Lombok
}