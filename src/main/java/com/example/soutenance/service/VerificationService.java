package com.example.soutenance.service;

import com.example.soutenance.exception.InvalidTokenException;
import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.VerificationToken;
import com.example.soutenance.repository.ApprenantRepository;
import com.example.soutenance.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {
    private final VerificationTokenRepository tokenRepository;
    private final ApprenantRepository apprenantsRepository;
    private final EmailService emailService;

    @Value("${app.verification.token.expiration}")
    private long tokenExpirationMinutes;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createVerificationToken(Apprenants etudiant) {
        log.info("===== START TOKEN CREATION =====");
        log.info("Creating token for student: {} (ID: {})", etudiant.getEmail(), etudiant.getId());

        // Generate token
        String tokenValue = UUID.randomUUID().toString();
        log.info("Generated token: {}", tokenValue);

        try {
            // Create token entity
            VerificationToken token = VerificationToken.builder()
                    .token(tokenValue)
                    .etudiant(etudiant)
                    .expiryDate(LocalDateTime.now().plusMinutes(tokenExpirationMinutes))
                    .used(false)
                    .build();

            // Save token
            log.info("Attempting to save token to database...");
            VerificationToken savedToken = tokenRepository.saveAndFlush(token);
            // Force database refresh
            tokenRepository.flush();

            log.info("Token saved successfully with ID: {}", savedToken.getId(), savedToken.getToken());

            // Verify save
            tokenRepository.findByToken(tokenValue).ifPresentOrElse(
                    t -> log.info("Token verified in database with ID: {}", t.getId()),
                    () -> log.error("Token not found in database after save!")
            );

            log.info("===== TOKEN CREATION COMPLETE =====");

            VerificationToken verifiedToken = tokenRepository.findByToken(tokenValue)
                    .orElseThrow(() -> new RuntimeException("Token not found after save"));
            log.info("Token verified in database: {}", verifiedToken.getToken());

            boolean tokenExists = tokenRepository.existsByToken(tokenValue);
            log.info("Token exists in database: {}", tokenExists);

            if (!tokenExists) {
                throw new RuntimeException("Token was not saved properly");
            }

            return tokenValue;

        } catch (Exception e) {
            log.error("Error creating token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create token", e);
        }
    }

    @Transactional
    public void verifyEmail(String token) {
        log.info("===== START TOKEN VERIFICATION =====");
        log.info("Attempting to verify token: {}", token);

        try {
            VerificationToken verificationToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> {
                        log.error("Token not found in database: {}", token);
                        return new InvalidTokenException("Token invalide");
                    });

            log.info("Token found in database. Token ID: {}, Student: {}",
                    verificationToken.getId(),
                    verificationToken.getEtudiant().getEmail());

            if (verificationToken.isUsed()) {
                log.error("Token already used");
                throw new InvalidTokenException("Token déjà utilisé");
            }

            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                log.error("Token expired");
                throw new InvalidTokenException("Token expiré");
            }

            // Update student
            Apprenants etudiant = verificationToken.getEtudiant();
            etudiant.setEmailVerified(true);
            apprenantsRepository.save(etudiant);
            log.info("Student email verified: {}", etudiant.getEmail());

            // Mark token as used
            verificationToken.setUsed(true);
            verificationToken.setConfirmedDate(LocalDateTime.now());
            tokenRepository.save(verificationToken);
            log.info("Token marked as used");

            emailService.sendWelcomeEmail(etudiant);

            log.info("===== TOKEN VERIFICATION COMPLETE =====");

        } catch (InvalidTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during verification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify token", e);
        }
    }
}
