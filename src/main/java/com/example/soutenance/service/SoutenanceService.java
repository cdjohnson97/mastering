package com.example.soutenance.service;

import com.example.soutenance.dto.InscriptionRequest;
import com.example.soutenance.dto.SoutenanceRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class SoutenanceService {
    private final SoutenanceRepository soutenanceRepository;
    private final ApprenantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final VerificationService verificationService;
    private final EmailService emailService;

    public Soutenance creerSoutenance(SoutenanceRequest request) {
        return soutenanceRepository.save(
                Soutenance.builder()
                        .dateHeure(request.getDateHeure())
                        .lieu(request.getLieu())
                        .sujet(request.getSujet())
                        .build()
        );
    }

    public List<Soutenance> getAllSoutenances() {
        return soutenanceRepository.findAll();
    }

    public Inscription inscrireEtudiant(Long soutenanceId, InscriptionRequest request) {
        Soutenance soutenance = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new SoutenanceNotFoundException(soutenanceId));

        // Check if student exists or create new one
        Apprenants apprenant = etudiantRepository.findByEmail(request.getEmailEtudiant())
                .orElseGet(() -> {
                    // Create new student
                    Apprenants newApprenant = Apprenants.builder()
                            .email(request.getEmailEtudiant())
                            .emailVerified(false)

                            .build();
                    return etudiantRepository.save(newApprenant);
                });

        // Save inscription
        Inscription inscription = inscriptionRepository.save(
                Inscription.builder()
                        .soutenance(soutenance)
                        .etudiant(apprenant)
                        .creneauHoraire(request.getCreneauHoraire())
                        .statut(StatutInscription.EN_ATTENTE)
                        .build()
        );

        // Send verification email if student is not verified

        if (!apprenant.isEmailVerified()) {
            String token = verificationService.createVerificationToken(apprenant);  // Use the instance
            emailService.sendVerificationEmail(apprenant, token);
        }


        return inscription;
    }


    public List<Apprenants> getEtudiantsBySoutenanceId(Long soutenanceId) {
        return inscriptionRepository.findEtudiantsBySoutenanceId(soutenanceId);
    }
}