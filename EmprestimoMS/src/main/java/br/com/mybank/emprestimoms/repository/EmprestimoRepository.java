package br.com.mybank.emprestimoms.repository;

import br.com.mybank.emprestimoms.domain.Emprestimo;
import br.com.mybank.emprestimoms.domain.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {
}
