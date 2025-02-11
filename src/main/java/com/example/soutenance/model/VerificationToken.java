package com.example.soutenance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


    @Entity
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class VerificationToken {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String token;

        @OneToOne(targetEntity = Apprenants.class, fetch = FetchType.EAGER)
        @JoinColumn(nullable = false, name = "etudiant_id")
        private Apprenants etudiant;

        private LocalDateTime expiryDate;

        @Builder.Default
        private boolean used = false;

        @Builder.Default
        private boolean emailVerified = false;

        private LocalDateTime confirmedDate;

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryDate);
        }
    }


