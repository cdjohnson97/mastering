package com.example.soutenance.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
    @Builder
    public class SoutenanceDetailRequest {
        private Long id;
        private String sujet;
        private String lieu;
        private LocalDateTime dateHeure;
        private List<SessionApparentRequest> etudiants;
    }

