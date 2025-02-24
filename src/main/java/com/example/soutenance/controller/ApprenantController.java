package com.example.soutenance.controller;

import com.example.soutenance.dto.ApprenantDetailDTO;
import com.example.soutenance.dto.ApprenantRequest;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.service.ApprenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/apprenants")
@RequiredArgsConstructor
public class ApprenantController {

    private final ApprenantService apprenantService;

    @PostMapping
    public ResponseEntity<Apprenants> creerEtudiant(@Valid @RequestBody ApprenantRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apprenantService.creerEtudiant(dto));
    }

    @GetMapping
    public ResponseEntity<List<ApprenantDetailDTO>> getAllEtudiants() {
        return ResponseEntity.ok(apprenantService.getAllEtudiantsWithDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprenantDetailDTO> getEtudiantDetails(@PathVariable Long id) {
        return ResponseEntity.ok(apprenantService.getEtudiantDetails(id));
    }


}

