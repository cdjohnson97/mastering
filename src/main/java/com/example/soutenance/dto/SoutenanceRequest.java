package com.example.soutenance.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoutenanceRequest {
    @NotNull
    @Future
    private LocalDateTime dateHeure;

    @NotBlank
    private String lieu;

    private String sujet;
}