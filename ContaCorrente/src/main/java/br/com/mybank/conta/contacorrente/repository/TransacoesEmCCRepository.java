package br.com.mybank.conta.contacorrente.repository;

import br.com.mybank.conta.contacorrente.domain.TransacoesEmCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacoesEmCCRepository extends JpaRepository<TransacoesEmCC, Integer> {

}
