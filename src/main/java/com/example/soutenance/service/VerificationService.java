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

    public String createVerificationToken(Apprenants etudiant) {
        String token = generateUniqueToken();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .etudiant(etudiant)
                .expiryDate(LocalDateTime.now().plusMinutes(tokenExpirationMinutes))
                .build();

        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(etudiant, token);
        return token;
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token invalide"));

        if (verificationToken.isUsed()) {
            throw new InvalidTokenException("Token déjà utilisé");
        }

        if (verificationToken.isExpired()) {
            throw new InvalidTokenException("Token expiré");
        }

        Apprenants etudiant = verificationToken.getEtudiant();
        apprenantsRepository.save(etudiant);

        verificationToken.setUsed(true);
        verificationToken.setConfirmedDate(LocalDateTime.now());
        tokenRepository.save(verificationToken);
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}
