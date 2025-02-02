package com.example.soutenance.repository;

import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.model.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findBySoutenanceId(Long soutenanceId);
    List<Inscription> findByEtudiantEmail(String email);

    boolean existsBySoutenanceAndEtudiant(Soutenance soutenance, Apprenants etudiant);
}

