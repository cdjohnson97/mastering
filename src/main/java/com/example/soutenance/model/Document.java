package com.example.soutenance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    @JsonIgnoreProperties("documents")
    @JsonBackReference
    private Apprenants etudiant;

    private String documentType; // SCHOOL_CERTIFICATE, ID_DOCUMENT, etc.
    private String fileName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PENDING;
}
