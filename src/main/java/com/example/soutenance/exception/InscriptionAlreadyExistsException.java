package com.example.soutenance.exception;

public class InscriptionAlreadyExistsException extends RuntimeException {
    public InscriptionAlreadyExistsException(Long soutenanceId, String email) {
        super("Inscription existe déjà pour la soutenance " + soutenanceId + " et l'étudiant " + email);
    }
}