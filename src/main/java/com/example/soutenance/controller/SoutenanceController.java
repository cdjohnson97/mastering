package com.example.soutenance.controller;
import com.example.soutenance.dto.InscriptionRequest;
import com.example.soutenance.dto.SessionApparentRequest;
import com.example.soutenance.dto.SoutenanceDetailRequest;
import com.example.soutenance.dto.SoutenanceRequest;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.model.Soutenance;
import com.example.soutenance.model.StatutInscription;
import com.example.soutenance.repository.InscriptionRepository;
import com.example.soutenance.service.SoutenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soutenances")
@RequiredArgsConstructor
public class SoutenanceController {
    private final SoutenanceService soutenanceService;
    private final InscriptionRepository inscriptionRepository;

    @PostMapping
    public ResponseEntity<Soutenance> creerSoutenance(@Valid @RequestBody SoutenanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(soutenanceService.creerSoutenance(request));
    }

    @PostMapping("/{soutenanceId}/inscriptions")
    public ResponseEntity<Inscription> inscrireApprenant(
            @PathVariable Long soutenanceId,
            @Valid @RequestBody InscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(soutenanceService.inscrireEtudiant(soutenanceId, request));
    }

    @GetMapping("/{soutenanceId}/etudiants")
    public ResponseEntity<List<SessionApparentRequest>> getEtudiantsBySoutenance(
            @PathVariable Long soutenanceId
    ) {
        List<Apprenants> etudiants = soutenanceService.getEtudiantsBySoutenanceId(soutenanceId);

        List<SessionApparentRequest> dtos = etudiants.stream()
                .map(e -> SessionApparentRequest.builder()
                        .id(e.getId())
                        .nom(e.getNom())
                        .prenom(e.getPrenom())
                        .email(e.getEmail())
                        .statut(e.getStatus()) // Méthode à implémenter
                        .build())
                        .toList();

        return ResponseEntity.ok(dtos);
    }

    private StatutInscription getStatutInscription(Long soutenanceId, Long etudiantId) {
        List<Inscription> inscriptions = inscriptionRepository.findBySoutenanceIdAndEtudiantId(soutenanceId, etudiantId);
        return inscriptions.isEmpty() ?
                StatutInscription.APPRENANT :
                inscriptions.get(0).getStatut();  // Or implement logic to determine which status to return if multiple exist
    }


    @GetMapping
    public ResponseEntity<List<SoutenanceDetailRequest>> getAllSoutenances() {
        List<Soutenance> soutenances = soutenanceService.getAllSoutenances();

        List<SoutenanceDetailRequest> response = soutenances.stream()
                .map(soutenance -> SoutenanceDetailRequest.builder()
                        .id(soutenance.getId())
                        .sujet(soutenance.getSujet())
                        .lieu(soutenance.getLieu())
                        .dateHeure(soutenance.getDateHeure())
                        .etudiants(getEtudiantsWithStatus(soutenance.getId()))
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    private List<SessionApparentRequest> getEtudiantsWithStatus(Long soutenanceId) {
        List<Apprenants> etudiants = soutenanceService.getEtudiantsBySoutenanceId(soutenanceId);

        return etudiants.   stream()
                .map(e -> SessionApparentRequest.builder()
                        .id(e.getId())
                        .nom(e.getNom())
                        .prenom(e.getPrenom())
                        .email(e.getEmail())
                        .statut(e.getStatus())
                        .build())
                .toList();
    }
}


