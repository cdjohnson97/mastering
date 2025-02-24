package com.example.soutenance.service;

import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Document;
import com.example.soutenance.model.Soutenance;
import com.example.soutenance.repository.SoutenanceRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SoutenanceRepository soutenanceRepository;


    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(Apprenants etudiant, String token) {
        try {
            log.info("Preparing to send verification email to: {}", etudiant.getEmail());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(etudiant.getEmail());
            helper.setSubject("Vérification de votre email");
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;
            log.info("Verification URL: {}", verificationUrl);
            log.info("Full verification URL: {}", verificationUrl);


            String htmlContent = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <style>
                                    .email-container {
                                        font-family: Arial, sans-serif;
                                        max-width: 600px;
                                        margin: 0 auto;
                                        padding: 20px;
                                    }
                                    .header {
                                        background-color: #4A90E2;
                                        color: white;
                                        padding: 20px;
                                        text-align: center;
                                        border-radius: 5px 5px 0 0;
                                    }
                                    .content {
                                        background-color: #ffffff;
                                        padding: 20px;
                                        border: 1px solid #dedede;
                                        border-radius: 0 0 5px 5px;
                                    }
                                    .button {
                                        background-color: #4CAF50;
                                        color: white;
                                        padding: 15px 25px;
                                        text-decoration: none;
                                        border-radius: 5px;
                                        display: inline-block;
                                        margin: 20px 0;
                                    }
                                    .footer {
                                        text-align: center;
                                        margin-top: 20px;
                                        color: #666;
                                        font-size: 12px;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="email-container">
                                    <div class="header">
                                        <h1>Mastering Service</h1>
                                    </div>
                                    <div class="content">
                                        <h2>Bonjour %s,</h2>
                                        <p>Pour commencer votre procédure d'inscription pour la soutenance, veuillez vérifier votre adresse email.</p>
                                        <div style="text-align: center;">
                                            <a href="%s?token=%s" class="button">
                                                Vérifier mon email
                                            </a>
                                        </div>
                                        <p>Ce lien est valable pendant 24 heures.</p>
                                        <p>Si vous n'avez pas demandé cette vérification, veuillez ignorer cet email.</p>
                                    </div>
                                    <div class="footer">
                                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                                        <p>Mastering© 2025. Tous droits réservés.</p>
                                        <p>Chance Darlon Dev  :)</p>
                                    </div>
                                </div>
                            </body>
                            </html>
                            """,
                    etudiant.getPrenom(),
                    frontendUrl,
                    token
            );

            helper.setText(htmlContent, true); // true indicates HTML content
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", etudiant.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }


    public void sendWelcomeEmail(Apprenants etudiant) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(etudiant.getEmail());
            helper.setSubject("Bienvenue ! Choisissez votre soutenance");

            // Get soutenances list
            List<Soutenance> soutenances = soutenanceRepository.findAll();
            StringBuilder soutenancesList = new StringBuilder();
            soutenances.forEach(s ->
                    soutenancesList.append(String.format(
                            "<div style='margin-bottom: 15px; padding: 10px; border: 1px solid #eee; border-radius: 5px;'>" +
                                    "<strong>%s</strong><br>" +
                                    "Date: %s<br>" +
                                    "Lieu: %s" +
                                    "</div>",
                            s.getSujet(),
                            s.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            s.getLieu()
                    ))
            );

            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        .email-container {
                            font-family: Arial, sans-serif;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background-color: #4A90E2;
                            color: white;
                            padding: 20px;
                            text-align: center;
                            border-radius: 5px 5px 0 0;
                        }
                        .content {
                            background-color: #ffffff;
                            padding: 20px;
                            border: 1px solid #dedede;
                            border-radius: 0 0 5px 5px;
                        }
                        .button {
                            background-color: #4CAF50;
                            color: white;
                            padding: 15px 25px;
                            text-decoration: none;
                            border-radius: 5px;
                            display: inline-block;
                            margin: 20px 0;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 20px;
                            color: #666;
                            font-size: 12px;
                        }
                        .soutenance-list {
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="header">
                            <h1>Mastering Service</h1>
                        </div>
                        <div class="content">
                            <h2>Bienvenue %s !</h2>
                            <p>Votre email a été vérifié avec succès. Vous pouvez maintenant choisir votre soutenance.</p>
                            
                            <div class="soutenance-list">
                                <h3>Soutenances disponibles :</h3>
                                %s
                            </div>

                            <div style="text-align: center;">
                                <a href="%s/inscription" class="button">
                                    S'inscrire à une soutenance
                                </a>
                            </div>
                        </div>
                        <div class="footer">
                            <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                            <p>Mastering© 2025. Tous droits réservés.</p>
                            <p>Chance Darlon Dev  :)</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                    etudiant.getPrenom(),
                    soutenancesList.toString(),
                    frontendUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Welcome email sent to: {}", etudiant.getEmail());

        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    public void sendDocumentSubmissionConfirmation(Apprenants etudiant, Document document) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(etudiant.getEmail());
        message.setSubject("Confirmation de soumission de document");
        message.setText(String.format(
                "Bonjour %s,\n\n" +
                        "Nous confirmons la réception de votre document '%s'.\n" +
                        "Le document est en cours d'examen par notre équipe.\n" +
                        "Vous serez notifié une fois la vérification terminée.\n\n" +
                        "Cordialement",
                etudiant.getPrenom(),
                document.getDocumentType()
        ));
        mailSender.send(message);
        log.info("Document submission confirmation email sent to: {}", etudiant.getEmail());
    }

    public void sendDocumentStatusUpdate(Document document) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(document.getEtudiant().getEmail());
        message.setSubject("Mise à jour du statut de votre document");
        message.setText(String.format(
                "Bonjour %s,\n\n" +
                        "Le statut de votre document '%s' a été mis à jour à : %s\n\n" +
                        "Cordialement",
                document.getEtudiant().getPrenom(),
                document.getDocumentType(),
                document.getStatus()
        ));
        mailSender.send(message);
        log.info("Document status update email sent to: {}", document.getEtudiant().getEmail());
    }

    public void sendProfileVerificationComplete(Apprenants etudiant) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(etudiant.getEmail());
        message.setSubject("Profil vérifié avec succès");
        message.setText(String.format(
                "Bonjour %s,\n\n" +
                        "Nous sommes heureux de vous informer que tous vos documents ont été vérifiés et approuvés.\n" +
                        "Votre profil est maintenant complètement vérifié.\n" +
                        "Vous pouvez maintenant procéder à vos inscriptions aux soutenances.\n\n" +
                        "Cordialement",
                etudiant.getPrenom()
        ));
        mailSender.send(message);
        log.info("Profile verification complete email sent to: {}", etudiant.getEmail());
    }

    public void sendDocumentCommentNotification(Apprenants etudiant, String documentType, String comment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(etudiant.getEmail());
        message.setSubject("Nouveau commentaire sur votre document");
        message.setText(String.format(
                "Bonjour %s,\n\n" +
                        "Un administrateur a laissé un commentaire sur votre document '%s':\n\n" +
                        "\"%s\"\n\n" +
                        "Veuillez en prendre connaissance et effectuer les modifications nécessaires.\n\n" +
                        "Cordialement",
                etudiant.getPrenom(),
                documentType,
                comment
        ));

        mailSender.send(message);
        log.info("Document comment notification sent to: {}", etudiant.getEmail());
    }

    public void sendDocumentCommentNotification(
            Apprenants etudiant,
            String documentType,
            String comment,
            String commentedBy
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(etudiant.getEmail());
            message.setSubject("Nouveau commentaire sur votre document");
            message.setText(String.format(
                    "Bonjour %s,\n\n" +
                            "Un commentaire a été ajouté par %s sur votre document '%s':\n\n" +
                            "\"%s\"\n\n" +
                            "Veuillez consulter votre espace personnel pour plus de détails.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe administrative",
                    etudiant.getPrenom(),
                    commentedBy,
                    documentType,
                    comment
            ));

            mailSender.send(message);
            log.info("Comment notification email sent to: {}", etudiant.getEmail());
        } catch (Exception e) {
            log.error("Failed to send comment notification email: {}", e.getMessage());
            // Don't throw exception here to prevent transaction rollback
        }

    }

        public void sendProfileUnderVerificationEmail(Apprenants etudiant, Document document) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(etudiant.getEmail());
                message.setSubject("Document soumis - Profil en cours de vérification");

                message.setText(String.format(
                        "Bonjour %s,\n\n" +
                                "Nous confirmons que votre document '%s' a été soumis avec succès.\n\n" +
                                "Votre profil est maintenant en cours de vérification par notre équipe administrative.\n" +
                                "Vous serez notifié par email une fois la vérification terminée.\n\n" +
                                "Cordialement,\n" +
                                "L'équipe administrative",
                        etudiant.getPrenom(),
                        document.getDocumentType()
                ));

                mailSender.send(message);
                log.info("Profile under verification email sent to: {}", etudiant.getEmail());

            } catch (Exception e) {
                log.error("Failed to send profile verification email: {}", e.getMessage());
            }
        }
    }



