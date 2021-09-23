package br.com.mybank.mybank.repository;

import br.com.mybank.mybank.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    public Optional<Usuario> findLoginById(String id);


    Optional<Usuario> findByUsuario(String usuario);
}
