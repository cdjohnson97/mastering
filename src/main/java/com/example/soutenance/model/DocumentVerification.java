package com.example.soutenance.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonManagedReference  // Add this
    private Document document;

    private String adminComment;
    private LocalDateTime verificationDate;
    private String verifiedBy;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @OneToMany(mappedBy = "verification", cascade = CascadeType.ALL)
    private List<VerificationComment> comments = new ArrayList<>();
}