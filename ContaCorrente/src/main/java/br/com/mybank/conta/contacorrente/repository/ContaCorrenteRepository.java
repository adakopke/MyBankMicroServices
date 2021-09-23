package br.com.mybank.conta.contacorrente.repository;

import br.com.mybank.conta.contacorrente.domain.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {
}
