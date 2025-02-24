package com.example.soutenance.repository;

import com.example.soutenance.model.DocumentStatus;
import com.example.soutenance.model.DocumentVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentVerificationRepository extends JpaRepository<DocumentVerification, Long> {
    List<DocumentVerification> findByStatus(DocumentStatus status);
}
