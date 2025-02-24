package com.example.soutenance.repository;

import com.example.soutenance.model.VerificationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCommentRepository extends JpaRepository<VerificationComment, Long> {
}