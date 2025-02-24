package com.example.soutenance.controller;

import com.example.soutenance.dto.ApiResponse;
import com.example.soutenance.dto.InscriptionRequest;
import com.example.soutenance.exception.EmailNotVerifiedException;
import com.example.soutenance.exception.InscriptionAlreadyExistsException;
import com.example.soutenance.model.Inscription;
import com.example.soutenance.service.InscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
@Slf4j
public class InscriptionController {
    private final InscriptionService inscriptionService;

    @PostMapping
    public ResponseEntity<?> creerInscription(@Valid @RequestBody InscriptionRequest request) {
        try {
            Inscription inscription = inscriptionService.creerInscription(request);
            return ResponseEntity.ok(inscription);
        } catch (EmailNotVerifiedException e) {
            // This is expected when email needs verification
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (InscriptionAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during inscription: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Apprenant inexistant"));
        }
    }

}

