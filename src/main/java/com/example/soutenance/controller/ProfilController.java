package com.example.soutenance.controller;

import com.example.soutenance.dto.ProfilSetupRequest;
import com.example.soutenance.service.ProfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profil")
@RequiredArgsConstructor
public class ProfilController {
    private final ProfilService profilService;

    @PostMapping("/setup")
    public ResponseEntity<?> setupProfil(@Valid @RequestBody ProfilSetupRequest request) {
        return ResponseEntity.ok(profilService.setupProfil(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfil(@RequestParam String email) {
        return ResponseEntity.ok(profilService.getProfilByEmail(email));
    }
}