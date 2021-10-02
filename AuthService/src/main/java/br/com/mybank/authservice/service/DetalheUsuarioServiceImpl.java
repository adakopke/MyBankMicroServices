package br.com.mybank.authservice.service;
import br.com.mybank.authservice.data.DetalheUsuarioData;
import br.com.mybank.authservice.domain.Usuario;
import br.com.mybank.authservice.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DetalheUsuarioServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsuario(usuario);

        if (usuarioOptional.isEmpty()) {
            throw new UsernameNotFoundException("Usuário: " + usuario + " não encontrado");
        }

        return new DetalheUsuarioData(usuarioOptional);
    }
}
