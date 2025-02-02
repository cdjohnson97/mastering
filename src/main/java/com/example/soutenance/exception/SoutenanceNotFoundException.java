package com.example.soutenance.exception;

import lombok.Getter;

@Getter
public class SoutenanceNotFoundException extends RuntimeException {
    private final Long soutenanceId;

    public SoutenanceNotFoundException(Long soutenanceId) {
        super("Soutenance non trouvée avec l'id: " + soutenanceId);
        this.soutenanceId = soutenanceId;
    }
}