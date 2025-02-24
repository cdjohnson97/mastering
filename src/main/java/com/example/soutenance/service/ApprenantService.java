package com.example.soutenance.service;
import com.example.soutenance.dto.*;
import com.example.soutenance.exception.ResourceNotFoundException;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Document;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.repository.ApprenantRepository;
import com.example.soutenance.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.example.soutenance.dto.InscriptionDTO;

@Service
@RequiredArgsConstructor
public class ApprenantService {
    private final ApprenantRepository apprenantRepository;
    private final DocumentRepository documentRepository;
    public Apprenants creerEtudiant(ApprenantRequest dto) {
        Apprenants etudiant = Apprenants.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())

                .build();
        return apprenantRepository.save(etudiant);
    }

    public List<ApprenantDetailDTO> getAllEtudiantsWithDetails() {
        return apprenantRepository.findAll().stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());
    }

    public ApprenantDetailDTO getEtudiantDetails(Long id) {
        Apprenants etudiant = apprenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return convertToDetailDTO(etudiant);
    }

    private ApprenantDetailDTO convertToDetailDTO(Apprenants etudiant) {
        return ApprenantDetailDTO.builder()
                .id(etudiant.getId())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .email(etudiant.getEmail())
                .emailVerified(etudiant.isEmailVerified())
                .inscriptions(convertToInscriptionDTOs(etudiant.getInscriptions()))
                .documents(convertToDocumentDTOs(documentRepository.findByEtudiantId(etudiant.getId())))
                .build();
    }

    private List<InscriptionDTO> convertToInscriptionDTOs(List<Inscription> inscriptions) {
        return inscriptions.stream()
                .map(this::convertToInscriptionDTO)  // Extract to separate method
                .collect(Collectors.toList());
    }

    private InscriptionDTO convertToInscriptionDTO(Inscription inscription) {
        return InscriptionDTO.builder()
                .id(inscription.getId())
                .soutenanceId(inscription.getSoutenance().getId())
                .soutenanceSujet(inscription.getSoutenance().getSujet())
                .emailEtudiant(inscription.getEtudiant().getEmail())
                .creneauHoraire(inscription.getCreneauHoraire())
                .statut(inscription.getStatut())
                .build();
    }

    private List<DocumentDTO> convertToDocumentDTOs(List<Document> documents) {
        return documents.stream()
                .map(document -> DocumentDTO.builder()
                        .id(document.getId())
                        .documentType(document.getDocumentType())
                        .fileName(document.getFileName())
                            .status(document.getStatus())
                        .uploadDate(document.getUploadDate())
                        .build())
                .collect(Collectors.toList());
    }

}
