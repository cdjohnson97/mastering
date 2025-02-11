package com.example.soutenance.service;

import com.example.soutenance.model.Apprenants;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(Apprenants etudiant, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(etudiant.getEmail());
        message.setSubject("Vérification de votre adresse email");

        String emailContent = String.format(
                "Bonjour %s,\n\n" +
                        "Pour vérifier votre adresse email, veuillez cliquer sur le lien suivant:\n" +
                        "%s/verify-email?token=%s\n\n" +
                        "Ce lien est valable pendant 24 heures.\n\n" +
                        "Cordialement",
                etudiant.getPrenom(),
                frontendUrl,
                token
        );

        message.setText(emailContent);
        mailSender.send(message);
    }
}

