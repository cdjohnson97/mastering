package com.example.soutenance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentDTO {
    private Long id;
    private String documentType;
    private String fileName;
    private LocalDateTime uploadDate;

}