package br.com.mybank.mybank.controller;


import br.com.mybank.mybank.domain.Usuario;
import br.com.mybank.mybank.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("adicionar")
    public Usuario adicionarUsuario(@RequestBody Usuario usuario) {

        //TODO tratamento de erros, verificar se um usuário exsite e response entity
        return usuarioService.adicionarUsuario(usuario);
    }

    @GetMapping("listartodos")
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }


    @GetMapping("listar/{id}")
    public ResponseEntity<?> listar(@PathVariable Integer id) {

        Optional<Usuario> usuario = usuarioService.listar(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    @GetMapping("validarsenha")
    public ResponseEntity<Boolean> validarSenha(@RequestParam String usuario, @RequestParam String senha) {
       return usuarioService.verificarSenha(usuario, senha);
    }

    @PutMapping("atualizar")
    public Usuario atualizarUsuario(@RequestBody Usuario usuario, @RequestHeader(value = "Authorization", required = true) String token) throws JSONException {
        return usuarioService.atualizarUsuario(usuario, token);
    }

    //TODO trocar para soft delete
    @DeleteMapping("remover/{id}")
    public void removerUsuario(@PathVariable Integer id) {
        usuarioService.removerPorID(id);
    }


}
