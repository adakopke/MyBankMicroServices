package br.com.mybank.contacorrentems.repository;
import br.com.mybank.contacorrentems.domain.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {
    public Optional<ContaCorrente> findByIdUsuario(Integer id);
    public Optional<ContaCorrente> findByNumeroConta(String id);
}
