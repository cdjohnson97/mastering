package com.example.soutenance.dto;

import com.example.soutenance.model.DocumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentWithStudentDTO {
    private Long id;
    private String documentType;
    private String fileName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadDate;
    private DocumentStatus status;
    // Add student info
    private String studentName;
    private String studentEmail;
}
