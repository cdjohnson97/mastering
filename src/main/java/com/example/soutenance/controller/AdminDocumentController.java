package com.example.soutenance.controller;

import com.example.soutenance.dto.CommentRequest;
import com.example.soutenance.dto.DocumentVerificationRequest;
import com.example.soutenance.dto.DocumentWithStudentDTO;
import com.example.soutenance.dto.VerificationCommentDTO;
import com.example.soutenance.exception.ResourceNotFoundException;
import com.example.soutenance.model.Document;
import com.example.soutenance.model.DocumentVerification;
import com.example.soutenance.model.VerificationComment;
import com.example.soutenance.service.DocumentVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/documents")
@RequiredArgsConstructor
public class AdminDocumentController {
    private final DocumentVerificationService verificationService;

    @GetMapping("/pending")
    public ResponseEntity<List<DocumentWithStudentDTO>> getPendingDocuments() {  // Update return type
        List<DocumentWithStudentDTO> documents = verificationService.getPendingDocuments();
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/{documentId}/verify")
    public ResponseEntity<DocumentVerification> verifyDocument(
            @PathVariable Long documentId,
            @RequestBody @Valid DocumentVerificationRequest request
    ) {
        return ResponseEntity.ok(verificationService.verifyDocument(documentId, request));
    }

    @PostMapping("/verification/{verificationId}/comment")
    public ResponseEntity<VerificationCommentDTO> addComment(
            @PathVariable Long verificationId,
            @RequestBody @Valid CommentRequest request
    ) {
        try {
            VerificationCommentDTO comment = verificationService.addComment(verificationId, request);
            return ResponseEntity.ok(comment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}