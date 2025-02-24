package com.example.soutenance.repository;

import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query("SELECT t FROM VerificationToken t WHERE t.token = :token")
    Optional<VerificationToken> findByToken(@Param("token") String token);
    boolean existsByToken(String token);
}