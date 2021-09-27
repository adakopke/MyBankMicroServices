package br.com.mybank.containvestimento.repository;

import br.com.mybank.containvestimento.domain.TransacoesEmCI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacoesEmCIRepository extends JpaRepository<TransacoesEmCI, Integer> {

}
