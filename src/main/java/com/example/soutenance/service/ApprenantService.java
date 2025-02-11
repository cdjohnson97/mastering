package com.example.soutenance.service;

import com.example.soutenance.dto.ApprenantRequest;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.StatutInscription;
import com.example.soutenance.repository.ApprenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprenantService {
    private final ApprenantRepository apprenantRepository;
    public Apprenants creerEtudiant(ApprenantRequest dto) {
        Apprenants etudiant = Apprenants.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .build();
        return apprenantRepository.save(etudiant);
    }
}
