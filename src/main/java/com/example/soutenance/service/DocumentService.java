package com.example.soutenance.service;

import com.example.soutenance.exception.ResourceNotFoundException;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.DocumentType;
import com.example.soutenance.model.StatutInscription;
import com.example.soutenance.repository.ApprenantRepository;
import com.example.soutenance.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import com.example.soutenance.model.Document;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final ApprenantRepository apprenantsRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public Document uploadDocument(Long etudiantId, String documentType, MultipartFile file) {
        try {
            // Find student
            Apprenants etudiant = apprenantsRepository.findById(etudiantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            // Create base upload directory if it doesn't exist
            File baseDirectory = new File(uploadDir);
            if (!baseDirectory.exists()) {
                baseDirectory.mkdirs();
            }

            // Create student-specific directory
            File studentDirectory = new File(baseDirectory, etudiantId.toString());
            if (!studentDirectory.exists()) {
                studentDirectory.mkdirs();
            }

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = studentDirectory.toPath().resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save document info in database
            Document document = Document.builder()
                    .etudiant(etudiant)
                    .documentType(documentType)
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .filePath(filePath.toString())
                    .uploadDate(LocalDateTime.now())
                    .build();

            Document savedDocument = documentRepository.save(document);

            // Check if all required documents are uploaded
            checkAndUpdateStudentStatus(etudiant);

            return savedDocument;

        } catch (IOException e) {
            log.error("Failed to save file", e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }
    private void checkAndUpdateStudentStatus(Apprenants etudiant) {
        boolean hasSchoolCertificate = documentRepository
                .existsByEtudiantIdAndDocumentType(etudiant.getId(), DocumentType.SCHOOL_CERTIFICATE.name());
        boolean hasIdDocument = documentRepository
                .existsByEtudiantIdAndDocumentType(etudiant.getId(), DocumentType.ID_DOCUMENT.name());

        if (hasSchoolCertificate && hasIdDocument) {
            etudiant.setStatus(StatutInscription.EN_VERIFICATION);  // Using existing enum
            apprenantsRepository.save(etudiant);
        }
    }

    public List<Document> getStudentDocuments(Long etudiantId) {
        return documentRepository.findByEtudiantId(etudiantId);
    }
}