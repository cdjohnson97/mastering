package com.example.soutenance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"soutenance_id", "etudiant_id"})
})
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "soutenance_id", nullable = false)
    @JsonBackReference(value = "soutenance-inscription")
    private Soutenance soutenance;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonBackReference(value = "etudiant-inscription")  // Add this
    private Apprenants etudiant;

    private LocalDateTime creneauHoraire;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutInscription statut = StatutInscription.EN_ATTENTE;
}
