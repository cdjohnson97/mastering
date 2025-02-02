package com.example.soutenance.controller;

import com.example.soutenance.dto.ApprenantRequest;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.service.ApprenantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



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
}