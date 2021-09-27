package br.com.mybank.mybank.controller;


import br.com.mybank.mybank.domain.Usuario;
import br.com.mybank.mybank.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> atualizarSenhaDoUsuario(@RequestBody String senha, @RequestHeader(value = "Authorization", required = true) String token) throws JSONException, IOException {
        return usuarioService.atualizarSenhaDoUsuario(senha, token);
    }

    @DeleteMapping("remover")
    public ResponseEntity<?> removerUsuario(@RequestHeader(value = "Authorization", required = true) String token) throws JSONException, IOException {
        return usuarioService.remover(token);
    }


}
