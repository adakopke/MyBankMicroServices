package br.com.mybank.containvestimento.repository;

import br.com.mybank.containvestimento.domain.ContaInvestimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaInvestimentoRepository extends JpaRepository<ContaInvestimento, Integer> {
    public Optional<ContaInvestimento> findByIdUsuario(Integer id);
    public Optional<ContaInvestimento> findByNumeroConta(String id);
}
