package br.com.mybank.authservice.service;

import br.com.mybank.authservice.domain.Usuario;
import br.com.mybank.authservice.repository.UsuarioRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public ResponseEntity<?> adicionarUsuario(Usuario usuario) {

        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário já cadastrado");
        }

        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(usuario));
    }


    public ResponseEntity<?> atualizarSenhaDoUsuario(String senha, String token) {

        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (!isValidBearerToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (listar((Integer) tokenJson.get("id")).isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Optional<Usuario> usuarioOptional = null;
        try {
            usuarioOptional = listar((Integer) tokenJson.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Usuario usuario = new Usuario();
        usuario.setId(usuarioOptional.get().getId());
        usuario.setUsuario(usuarioOptional.get().getUsuario());
        usuario.setAtivo(usuarioOptional.get().isAtivo());
        usuario.setSenha(encoder.encode(senha));
        return  ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(usuario));
    }


    public ResponseEntity<?> remover(String token) {

        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (!isValidBearerToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (listar((Integer) tokenJson.get("id")).isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Usuario usuario = null;
        try {
            usuario = listar((Integer) tokenJson.get("id")).get();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        return  ResponseEntity.status(HttpStatus.OK).body("Usuário desativado com sucesso");
    }

    private String jwtFilter(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        token.replace("Bearer", " ");
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        return payload;

}

    private Optional<Usuario> listar(Integer id) {
        return usuarioRepository.findById(id);

    }


    private boolean isValidBearerToken(String accessToken) throws IOException {
        boolean isValid = false;
        String token = accessToken.replace("Bearer ", "");
        try {
            long jwtExpiresAt = JWT.decode(token).getExpiresAt().getTime()/1000;
            long difference = jwtExpiresAt - (System.currentTimeMillis()/1000);
            if(difference >= 30){
                isValid = true;
            }
        } catch (JWTDecodeException exception){
            throw new IOException(exception.getMessage());
        }
        return isValid;
    }




}