package br.com.mybank.emprestimo.repository;

import br.com.mybank.emprestimo.domain.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {
}
