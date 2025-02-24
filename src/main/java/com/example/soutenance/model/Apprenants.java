package com.example.soutenance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apprenants {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String niveauEtude;
        private String specialite;

        private boolean emailVerified;

        @Enumerated(EnumType.STRING)
        private StatutInscription status = StatutInscription.EN_ATTENTE;

        @OneToMany(mappedBy = "etudiant")
        private List<Document> documents;

        @OneToMany(mappedBy = "etudiant")
        private List<Inscription> inscriptions;
}





