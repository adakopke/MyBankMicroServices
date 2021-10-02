package com.example.simuladorpixms.repository;
import com.example.simuladorpixms.domain.ChavesPix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChavePixRepository extends JpaRepository<ChavesPix, Integer> {
    public Optional<ChavesPix> findAllByChavePix(String chave);
}
