package br.com.mybank.authservice.data;
import br.com.mybank.authservice.domain.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
public class DetalheUsuarioData implements UserDetails {

    private final Optional<Usuario> usuario;

    public Integer getId() {
        return usuario.orElse(new Usuario()).getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return usuario.orElse(new Usuario()).getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.orElse(new Usuario()).getUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
