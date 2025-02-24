package com.example.soutenance.dto;

import com.example.soutenance.model.DocumentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentVerificationRequest {
    private DocumentStatus status;
    private String comment;
    private String adminUsername;
}

