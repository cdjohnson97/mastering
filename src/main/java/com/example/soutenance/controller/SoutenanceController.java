package com.example.soutenance.controller;

import com.example.soutenance.dto.InscriptionRequest;
import com.example.soutenance.dto.SoutenanceRequest;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.model.Soutenance;
import com.example.soutenance.service.SoutenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/soutenances")
@RequiredArgsConstructor
public class SoutenanceController {
    private final SoutenanceService soutenanceService;

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
        return ResponseEntity.ok(soutenanceService.inscrireEtudiant(soutenanceId, request));
    }
}
