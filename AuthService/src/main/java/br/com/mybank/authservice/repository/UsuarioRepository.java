package br.com.mybank.authservice.repository;

import br.com.mybank.authservice.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    public Optional<Usuario> findLoginById(String id);


    Optional<Usuario> findByUsuario(String usuario);
}
