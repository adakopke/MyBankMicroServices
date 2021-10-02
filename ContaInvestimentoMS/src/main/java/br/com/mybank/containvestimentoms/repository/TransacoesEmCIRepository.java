package br.com.mybank.containvestimentoms.repository;
import br.com.mybank.containvestimentoms.domain.TransacoesEmCI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacoesEmCIRepository extends JpaRepository<TransacoesEmCI, Integer> {

}
