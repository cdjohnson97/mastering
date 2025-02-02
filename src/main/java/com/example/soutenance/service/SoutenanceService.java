package com.example.soutenance.service;

import com.example.soutenance.dto.InscriptionRequest;
import com.example.soutenance.dto.SoutenanceRequest;
import com.example.soutenance.exception.ApprenantNotFoundException;
import com.example.soutenance.exception.SoutenanceNotFoundException;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.model.Soutenance;
import com.example.soutenance.repository.ApprenantRepository;
import com.example.soutenance.repository.InscriptionRepository;
import com.example.soutenance.repository.SoutenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoutenanceService {
    private final SoutenanceRepository soutenanceRepository;
    private final ApprenantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;

    public Soutenance creerSoutenance(SoutenanceRequest request) {
        return soutenanceRepository.save(
                Soutenance.builder()
                        .dateHeure(request.getDateHeure())
                        .lieu(request.getLieu())
                        .sujet(request.getSujet())
                        .build()
        );
    }

    public Inscription inscrireEtudiant(Long soutenanceId, InscriptionRequest request) {
        Soutenance soutenance = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new SoutenanceNotFoundException(soutenanceId));

        Apprenants apprenant = etudiantRepository.findByEmail(request.getEmailEtudiant())
                .orElseThrow(() -> new ApprenantNotFoundException(request.getEmailEtudiant()));

        return inscriptionRepository.save(
                Inscription.builder()
                        .soutenance(soutenance)
                        .etudiant(apprenant)
                        .creneauHoraire(request.getCreneauHoraire())
                        .build()
        );
    }
}