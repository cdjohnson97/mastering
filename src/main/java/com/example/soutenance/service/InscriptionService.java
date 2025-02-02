package com.example.soutenance.service;

import com.example.soutenance.dto.InscriptionRequest;
import com.example.soutenance.exception.ApprenantNotFoundException;
import com.example.soutenance.exception.InscriptionAlreadyExistsException;
import com.example.soutenance.exception.InscriptionNotFoundException;
import com.example.soutenance.exception.SoutenanceNotFoundException;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.model.Soutenance;
import com.example.soutenance.model.StatutInscription;
import com.example.soutenance.repository.ApprenantRepository;
import com.example.soutenance.repository.InscriptionRepository;
import com.example.soutenance.repository.SoutenanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionService {
    private final InscriptionRepository inscriptionRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final ApprenantRepository apprenantRepository;

    @Transactional
    public Inscription creerInscription(InscriptionRequest request) {
        // Vérifier l'existence de la soutenance
        Soutenance soutenance = soutenanceRepository.findById(request.getSoutenanceId())
                .orElseThrow(() -> new SoutenanceNotFoundException(request.getSoutenanceId()));

        // Vérifier l'existence de l'étudiant
        Apprenants etudiant = apprenantRepository.findByEmail(request.getEmailEtudiant())
                .orElseThrow(() -> new ApprenantNotFoundException(request.getEmailEtudiant()));

        // Vérifier les doublons
        if (inscriptionRepository.existsBySoutenanceAndEtudiant(soutenance, etudiant)) {
            throw new InscriptionAlreadyExistsException(request.getSoutenanceId(), request.getEmailEtudiant());
        }

        // Créer l'inscription
        Inscription inscription = Inscription.builder()
                .soutenance(soutenance)
                .etudiant(etudiant)
                .creneauHoraire(request.getCreneauHoraire())
                .statut(StatutInscription.EN_ATTENTE)
                .build();

        return inscriptionRepository.save(inscription);
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsBySoutenance(Long soutenanceId) {
        return inscriptionRepository.findBySoutenanceId(soutenanceId);
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByEtudiant(String emailEtudiant) {
        return inscriptionRepository.findByEtudiantEmail(emailEtudiant);
    }

    @Transactional
    public Inscription updateStatutInscription(Long inscriptionId, StatutInscription nouveauStatut) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new InscriptionNotFoundException(inscriptionId));

        inscription.setStatut(nouveauStatut);
        return inscriptionRepository.save(inscription);
    }

    @Transactional
    public void supprimerInscription(Long inscriptionId) {
        if (!inscriptionRepository.existsById(inscriptionId)) {
            throw new InscriptionNotFoundException(inscriptionId);
        }
        inscriptionRepository.deleteById(inscriptionId);
    }
}
