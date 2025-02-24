    package com.example.soutenance.service;

    import com.example.soutenance.dto.InscriptionRequest;
    import com.example.soutenance.exception.*;
    import com.example.soutenance.model.*;
    import com.example.soutenance.repository.ApprenantRepository;
    import com.example.soutenance.repository.InscriptionRepository;
    import com.example.soutenance.repository.SoutenanceRepository;

    import com.example.soutenance.repository.VerificationTokenRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class InscriptionService {
        private final InscriptionRepository inscriptionRepository;
        private final SoutenanceRepository soutenanceRepository;
        private final ApprenantRepository apprenantRepository;
        private final VerificationTokenRepository verificationTokenRepository;
        private final EmailService emailService;
        private final VerificationService verificationService;

        @Transactional
        public Inscription creerInscription(InscriptionRequest request) {
            log.info("Starting inscription process for email: {}", request.getEmailEtudiant());

            try {
                Soutenance soutenance = soutenanceRepository.findById(request.getSoutenanceId())
                        .orElseThrow(() -> new SoutenanceNotFoundException(request.getSoutenanceId()));

                Apprenants apprenant = apprenantRepository.findByEmail(request.getEmailEtudiant())
                        .orElseGet(() -> {
                            Apprenants newApprenant = Apprenants.builder()
                                    .email(request.getEmailEtudiant())
                                    .emailVerified(false)
                                    .status(StatutInscription.EN_ATTENTE)
                                    .build();
                            return apprenantRepository.save(newApprenant);
                        });

                log.info("Student email verification status: {}", apprenant.isEmailVerified());

                if (inscriptionRepository.existsBySoutenanceAndEtudiant(soutenance, apprenant)) {
                    throw new InscriptionAlreadyExistsException(request.getSoutenanceId(), request.getEmailEtudiant());
                }

                if (!apprenant.isEmailVerified()) {
                    // Create and send verification token
                    String token = verificationService.createVerificationToken(apprenant);
                    emailService.sendVerificationEmail(apprenant, token);

                    // If we reach here, email was sent successfully
                    throw new EmailNotVerifiedException("Veuillez vérifier votre email avant de procéder à l'inscription. Un email de vérification a été envoyé.");
                }

                // Create inscription for verified student
                Inscription inscription = Inscription.builder()
                        .soutenance(soutenance)
                        .etudiant(apprenant)
                        .creneauHoraire(request.getCreneauHoraire())
                        .statut(StatutInscription.EN_ATTENTE)
                        .build();

                return inscriptionRepository.save(inscription);

            } catch (EmailNotVerifiedException e) {
                // This is expected for unverified emails
                throw e;
            } catch (Exception e) {
                log.error("Error during inscription process: ", e);
                throw new RuntimeException("Une erreur est survenue lors de l'inscription: " + e.getMessage());
            }
        }

        @Transactional(readOnly = true)
        public List<Inscription> getInscriptionsBySoutenance(Long soutenanceId) {
            return inscriptionRepository.findBySoutenanceId(soutenanceId);
        }

        @Transactional(readOnly = true)
        public List<Inscription> getInscriptionsByEtudiant(String emailEtudiant) {
            return inscriptionRepository.findByEtudiantEmail(emailEtudiant);
        }

        @Transactional
        public Inscription updateStatutInscription(Long inscriptionId, StatutInscription nouveauStatut) {
            Inscription inscription = inscriptionRepository.findById(inscriptionId)
                    .orElseThrow(() -> new InscriptionNotFoundException(inscriptionId));

            inscription.setStatut(nouveauStatut);
            log.info("Updated inscription status to {} for ID: {}", nouveauStatut, inscriptionId);
            return inscriptionRepository.save(inscription);
        }

        @Transactional
        public void supprimerInscription(Long inscriptionId) {
            if (!inscriptionRepository.existsById(inscriptionId)) {
                throw new InscriptionNotFoundException(inscriptionId);
            }
            inscriptionRepository.deleteById(inscriptionId);
            log.info("Deleted inscription with ID: {}", inscriptionId);
        }

        @Transactional
        public void updateInscriptionStatusBasedOnDocuments(String emailEtudiant) {
            log.info("Updating inscription status based on documents for student: {}", emailEtudiant);

            List<Inscription> inscriptions = inscriptionRepository.findByEtudiantEmail(emailEtudiant);
            Apprenants etudiant = apprenantRepository.findByEmail(emailEtudiant)
                    .orElseThrow(() -> new ApprenantNotFoundException(emailEtudiant));

            if (etudiant.getStatus() == StatutInscription.VALIDEE) {
                log.info("Student documents validated, updating all inscriptions");
                inscriptions.forEach(inscription -> {
                    inscription.setStatut(StatutInscription.VALIDEE);
                    inscriptionRepository.save(inscription);
                    log.info("Updated inscription {} to VALIDEE", inscription.getId());
                });
            }
        }
    }