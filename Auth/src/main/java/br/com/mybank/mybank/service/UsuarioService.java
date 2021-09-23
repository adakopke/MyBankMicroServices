package br.com.mybank.mybank.service;

import br.com.mybank.mybank.domain.Usuario;
import br.com.mybank.mybank.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public Usuario adicionarUsuario(Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizarUsuario(Usuario usuario, String token) throws JSONException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));

        if (!usuarioRepository.findById((Integer) tokenJson.get("id")).get().getId().equals(usuario.getId())) {
            return null;
        }
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    //TODO Trocar esse void
    public void removerPorID(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public ResponseEntity<Boolean> verificarSenha(String usuario, String senha) {


        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsuario(usuario);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        boolean valid = encoder.matches(senha, usuarioOptional.get().getSenha());
        HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).body(valid);


    }

    private String jwtFilter(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        token.replace("Bearer", " ");
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        return payload;



}

    public Optional<Usuario> listar(Integer id) {
        return usuarioRepository.findById(id);

    }
}