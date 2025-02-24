package com.example.soutenance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerificationCommentDTO {
    private Long id;
    private String comment;
    private LocalDateTime commentDate;
    private String commentedBy;
}
