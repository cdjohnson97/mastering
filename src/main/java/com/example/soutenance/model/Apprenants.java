package com.example.soutenance.model;

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

        @Column(nullable = false)
        private String nom;

        @Column(nullable = false)
        private String prenom;

        @Column(unique = true, nullable = false)
        private String email;

        @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
        @Builder.Default
        private List<Inscription> inscriptions = new ArrayList<>();
    }

