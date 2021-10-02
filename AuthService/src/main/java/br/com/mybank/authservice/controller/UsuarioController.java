package br.com.mybank.authservice.controller;
import br.com.mybank.authservice.domain.Usuario;
import br.com.mybank.authservice.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("adicionar")
    public ResponseEntity<?> adicionarUsuario(@RequestBody Usuario usuario) {

        return usuarioService.adicionarUsuario(usuario);
    }

    @PutMapping("atualizar")
    public ResponseEntity<?> atualizarSenhaDoUsuario(@RequestBody String senha, @RequestHeader(value = "Authorization", required = true) String token) {
        return usuarioService.atualizarSenhaDoUsuario(senha, token);
    }

    @DeleteMapping("remover")
    public ResponseEntity<?> removerUsuario(@RequestHeader(value = "Authorization", required = true) String token) {
        return usuarioService.remover(token);
    }


}
