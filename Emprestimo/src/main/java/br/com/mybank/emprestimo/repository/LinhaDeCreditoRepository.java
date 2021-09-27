package br.com.mybank.emprestimo.repository;

import br.com.mybank.emprestimo.domain.LinhaDeCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinhaDeCreditoRepository extends JpaRepository<LinhaDeCredito, Integer> {
    public Optional<LinhaDeCredito> findByIdUsuario(Integer id);
}
