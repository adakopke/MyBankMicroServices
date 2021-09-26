package com.example.simuladorpixapi.repository;

import com.example.simuladorpixapi.domain.ChavesPix;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChavePixRepository extends JpaRepository<ChavesPix, Integer> {
    public Optional<ChavesPix> findAllByChavePix(String chave);
}
