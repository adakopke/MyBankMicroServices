package br.com.mybank.contacorrentems.repository;
import br.com.mybank.contacorrentems.domain.TransacoesEmCC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacoesEmCCRepository extends JpaRepository<TransacoesEmCC, Integer> {

}
