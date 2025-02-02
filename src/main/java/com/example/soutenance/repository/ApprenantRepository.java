package com.example.soutenance.repository;

import com.example.soutenance.model.Apprenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprenantRepository extends JpaRepository<Apprenants, Long> {
    Optional<Apprenants> findByEmail(String email);
}