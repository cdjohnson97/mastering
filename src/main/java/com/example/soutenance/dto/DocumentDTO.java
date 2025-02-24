package com.example.soutenance.dto;

import com.example.soutenance.model.DocumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentDTO {
    private Long id;
    private String documentType;
    private String fileName;
    private String fileType;
    private DocumentStatus status;  // Add this field
    private LocalDateTime uploadDate;
}