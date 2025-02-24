package com.example.soutenance.service;

import com.example.soutenance.dto.CommentRequest;
import com.example.soutenance.dto.DocumentVerificationRequest;
import com.example.soutenance.dto.DocumentWithStudentDTO;
import com.example.soutenance.dto.VerificationCommentDTO;
import com.example.soutenance.exception.ResourceNotFoundException;
import com.example.soutenance.model.*;
import com.example.soutenance.repository.ApprenantRepository;
import com.example.soutenance.repository.DocumentRepository;
import com.example.soutenance.repository.DocumentVerificationRepository;
import com.example.soutenance.repository.VerificationCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentVerificationService {
    private final DocumentRepository documentRepository;
    private final DocumentVerificationRepository verificationRepository;
    private final VerificationCommentRepository commentRepository;
    private final ApprenantRepository apprenantRepository;
    private final InscriptionService inscriptionService;
    private final EmailService emailService;

    @Transactional
    public DocumentVerification verifyDocument(Long documentId, DocumentVerificationRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        // Create verification record
        DocumentVerification verification = DocumentVerification.builder()
                .document(document)
                .status(request.getStatus())
                .adminComment(request.getComment())
                .verificationDate(LocalDateTime.now())
                .verifiedBy(request.getAdminUsername())
                .build();
        // Update document status
        document.setStatus(request.getStatus());
        documentRepository.save(document);

        // Update student status based on all their documents
        Apprenants etudiant = document.getEtudiant();
        updateStudentStatus(etudiant);

        inscriptionService.updateInscriptionStatusBasedOnDocuments(etudiant.getEmail());
        return verificationRepository.save(verification);
    }

    private void updateStudentStatus(Apprenants etudiant) {
        // Get all documents for this student
        List<Document> documents = documentRepository.findByEtudiantId(etudiant.getId());

        // Check if all documents are approved
        boolean allDocumentsApproved = documents.stream()
                .allMatch(doc -> doc.getStatus() == DocumentStatus.APPROVED);

        if (allDocumentsApproved) {
            etudiant.setStatus(StatutInscription.VALIDEE);
            emailService.sendProfileVerificationComplete(etudiant);
        } else if (documents.stream().anyMatch(doc -> doc.getStatus() == DocumentStatus.REJECTED)) {
            etudiant.setStatus(StatutInscription.REFUSEE);
        } else {
            etudiant.setStatus(StatutInscription.EN_ATTENTE);
        }

        apprenantRepository.save(etudiant);
    }



    @Transactional
    public VerificationCommentDTO addComment(Long verificationId, CommentRequest request) {
        DocumentVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));

        VerificationComment comment = VerificationComment.builder()
                .verification(verification)
                .comment(request.getComment())
                .commentDate(LocalDateTime.now())
                .commentedBy(request.getUsername())
                .build();

        verification.getComments().add(comment);
        VerificationComment savedComment = verificationRepository.save(verification)
                .getComments().get(verification.getComments().size() - 1);

        // Send email notification
        Document document = verification.getDocument();
        emailService.sendDocumentCommentNotification(
                document.getEtudiant(),
                document.getDocumentType(),
                request.getComment()
        );

        return convertToCommentDTO(savedComment);  // Changed method name to be more specific
    }

    // Add this conversion method
    private VerificationCommentDTO convertToCommentDTO(VerificationComment comment) {
        return VerificationCommentDTO.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .commentDate(comment.getCommentDate())
                .commentedBy(comment.getCommentedBy())
                .build();
    }

    public List<DocumentWithStudentDTO> getPendingDocuments() {
        return documentRepository.findByStatus(DocumentStatus.PENDING)
                .stream()
                .map(this::convertToDTO)  // Use separate method for clarity
                .collect(Collectors.toList());
    }

    private DocumentWithStudentDTO convertToDTO(Document document) {
        return DocumentWithStudentDTO.builder()
                .id(document.getId())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .filePath(document.getFilePath())
                .uploadDate(document.getUploadDate())
                .status(document.getStatus())
                .studentName(document.getEtudiant().getNom() + " " + document.getEtudiant().getPrenom())
                .studentEmail(document.getEtudiant().getEmail())
                .build();
    }
}







