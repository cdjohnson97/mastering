package com.example.soutenance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class InscriptionRequest {
    private Long soutenanceId; // Doit correspondre au nom
    private String emailEtudiant;
    private LocalDateTime creneauHoraire;

    // Getters/Setters obligatoires si pas Lombok
}