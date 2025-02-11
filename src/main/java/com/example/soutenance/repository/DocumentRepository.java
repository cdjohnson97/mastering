package com.example.soutenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.soutenance.model.Document;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEtudiantId(Long etudiantId);
    boolean existsByEtudiantIdAndDocumentType(Long etudiantId, String documentType);
}
