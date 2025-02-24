package com.example.soutenance.service;

import com.example.soutenance.model.Apprenants;
import com.example.soutenance.model.Document;
import com.example.soutenance.model.Inscription;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.mail.MessagingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SoutenanceRepository soutenanceRepository;
    private int documentDeadlineDays;


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
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(etudiant.getEmail());
            helper.setSubject("Profil vérifié avec succès");

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
                    .verification-badge {
                        background-color: #e8f5e9;
                        border-radius: 15px;
                        padding: 20px;
                        margin: 20px auto;
                        text-align: center;
                        max-width: 300px;
                    }
                    .verification-icon {
                        color: #4CAF50;
                        font-size: 48px;
                        margin-bottom: 10px;
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
                        <h1>Mastering service</h1>
                    </div>
                    <div class="content">
                        <h2>Félicitations %s !</h2>
                        
                        <div class="verification-badge">
                            <div class="verification-icon">✓</div>
                            <h3>Vérification Complète</h3>
                            <p>Tous vos documents ont été validés</p>
                        </div>
                        
                        <p>Nous sommes heureux de vous informer que tous vos documents ont été vérifiés et approuvés.</p>
                        <p>Votre profil est maintenant complètement vérifié et vous pouvez procéder au depôt de votre mémoire pour examen.</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/soutenances" class="button">
                                Voir les soutenances disponibles
                            </a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                        <p>Mastering© 2025. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                    etudiant.getPrenom(),
                    frontendUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Profile verification complete email sent to: {}", etudiant.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send profile verification email: {}", e.getMessage());
        }
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(etudiant.getEmail());
            helper.setSubject("Document soumis - Profil en cours de vérification");

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
                    .document-badge {
                        background-color: #e3f2fd;
                        border-radius: 10px;
                        padding: 15px;
                        margin: 20px 0;
                        border-left: 4px solid #2196F3;
                    }
                    .status-indicator {
                        text-align: center;
                        margin: 20px 0;
                    }
                    .status-badge {
                        display: inline-block;
                        background-color: #ffecb3;
                        color: #ff8f00;
                        padding: 8px 15px;
                        border-radius: 30px;
                        font-weight: bold;
                    }
                    .progress-container {
                        background-color: #f5f5f5;
                        border-radius: 10px;
                        height: 20px;
                        width: 100%%;
                        margin: 15px 0;
                    }
                    .progress-bar {
                        height: 100%%;
                        width: 30%%;
                        background-color: #4A90E2;
                        border-radius: 10px;
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
                        <h1>Mastering Service </h1>
                    </div>
                    <div class="content">
                        <h2>Bonjour %s,</h2>
                        
                        <div class="document-badge">
                            <h3>Document soumis avec succès</h3>
                            <p>Type: <strong>%s</strong></p>
                            <p>Date: <strong>%s</strong></p>
                        </div>
                        
                        <p>Votre document a bien été reçu et est en cours de traitement.</p>
                        
                        <div class="status-indicator">
                            <div class="status-badge">En cours de vérification</div>
                            <div class="progress-container">
                                <div class="progress-bar"></div>
                            </div>
                        </div>
                        
                        <p>Votre profil est maintenant en cours de vérification par notre équipe administrative. Ce processus peut prendre jusqu'à 48 heures ouvrables.</p>
                        <p>Vous recevrez une notification par email dès que la vérification sera terminée.</p>
                    </div>
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                        <p>Mastering© 2025. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                    etudiant.getPrenom(),
                    document.getDocumentType(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Profile under verification email sent to: {}", etudiant.getEmail());

        } catch (Exception e) {
            log.error("Failed to send profile verification email: {}", e.getMessage());
        }
    }

    public void sendProfilCompletedEmail(Apprenants etudiant) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(etudiant.getEmail());
            helper.setSubject("Profil complété avec succès");

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
                        <h2>Félicitations %s !</h2>
                        <p>Votre profil a été créé avec succès. Vous pouvez maintenant :</p>
                        <ul>
                            <li>Vous inscrire aux soutenances</li>
                            <li>Soumettre vos documents</li>
                            <li>Suivre l'état de vos inscriptions</li>
                        </ul>
                        <div style="text-align: center;">
                            <a href="%s/dashboard" class="button">
                                Accéder à mon espace
                            </a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Mastering© 2025. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                    etudiant.getPrenom(),
                    frontendUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Profile completion email sent to: {}", etudiant.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send profile completion email: {}", e.getMessage());
        }
    }





    public void sendSoutenanceInscriptionEmail(Inscription inscription) {
        try {
            Apprenants etudiant = inscription.getEtudiant();
            Soutenance soutenance = inscription.getSoutenance();

            // Valeurs par défaut si non définies dans les propriétés
            int docsDeadlineDays = documentDeadlineDays > 0 ? documentDeadlineDays : 14;
            int memoireDeadlineDays = docsDeadlineDays / 2; // Moitié du délai pour les docs administratifs

            // Vérifier que les dates sont différentes de la date de soutenance
            LocalDateTime soutenanceDate = soutenance.getDateHeure();
            LocalDateTime documentsDeadline = soutenanceDate.minusDays(docsDeadlineDays);
            LocalDateTime memoireDeadline = soutenanceDate.minusDays(memoireDeadlineDays);

            // Ajouter des logs pour vérifier les dates
            log.info("Soutenance date: {}", soutenanceDate);
            log.info("Documents deadline: {}", documentsDeadline);
            log.info("Mémoire deadline: {}", memoireDeadline);

            String formattedSoutenanceDate = soutenanceDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String formattedDocumentsDeadline = documentsDeadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String formattedMemoireDeadline = memoireDeadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Vérifier dans les logs si les dates formatées sont différentes
            log.info("Formatted soutenance date: {}", formattedSoutenanceDate);
            log.info("Formatted documents deadline: {}", formattedDocumentsDeadline);
            log.info("Formatted mémoire deadline: {}", formattedMemoireDeadline);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(etudiant.getEmail());
            helper.setSubject("Confirmation d'inscription à la soutenance");

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
                    .deadlines {
                        background-color: #fff9e6;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                    }
                    .documents {
                        margin-top: 20px;
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
                        <h1>Mastering service</h1>
                    </div>
                    <div class="content">
                        <h2>Félicitations %s !</h2>
                        <p>Votre inscription à la soutenance <strong>%s</strong> prévue le <strong>%s</strong> à <strong>%s</strong> a été enregistrée.</p>
                        
                        <div class="deadlines">
                            <h3>⚠️ Dates importantes à retenir :</h3>
                            <p><strong>%s</strong> : Date limite pour compléter votre profil et soumettre vos documents administratifs</p>
                            <p><strong>%s</strong> : Date limite pour soumettre votre mémoire</p>
                        </div>
                        
                        <div class="documents">
                            <h3>Documents requis :</h3>
                            <ul>
                                <li>Carte d'identité / Passeport</li>
                                <li>Certificat de scolarité</li>
                                <li>Mémoire (format PDF)</li>
                            </ul>
                        </div>
                        
                        <p>Pour que votre candidature soit validée et votre profile vérifié,  il est impératif de respecter ces délais.</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/profil/documents" class="button">
                                Compléter mon dossier
                            </a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                        <p>Mastering© 2025. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                    etudiant.getPrenom(),
                    soutenance.getSujet(),
                    soutenance.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    soutenance.getLieu(),
                    formattedDocumentsDeadline,  // Première date limite (documents administratifs)
                    formattedMemoireDeadline,    // Deuxième date limite (mémoire)
                    frontendUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Soutenance inscription confirmation email sent to: {}", etudiant.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send soutenance inscription email: {}", e.getMessage());
        }
    }
    }




