package com.example.soutenance.exception;

import lombok.Getter;

@Getter
public class ApprenantNotFoundException extends RuntimeException {
    private final String email;

    public ApprenantNotFoundException(String email) {
        super("Apprenant non trouvé avec l'email: " + email);
        this.email = email;
    }
}