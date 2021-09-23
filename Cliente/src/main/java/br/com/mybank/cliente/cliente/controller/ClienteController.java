package br.com.mybank.cliente.cliente.controller;


import br.com.mybank.cliente.cliente.domain.Pessoa;
import br.com.mybank.cliente.cliente.service.ClienteService;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente")
@AllArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;


    @PostMapping("adicionar")
    public ResponseEntity<?> adicionarCliente(@RequestBody Pessoa pessoa, @RequestHeader(value = "Authorization", required = true) String token) throws JSONException, IOException {
        return clienteService.adicionarCliente(pessoa, token);
    }

    @GetMapping("listartodos")
    public List<Pessoa> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("pesquisar/{id}")
    public Optional<Pessoa> listarPorId(@PathVariable Integer id) {
        return clienteService.listarPorIdUsuario(id);
    }

    @PutMapping("atualizar")
    public ResponseEntity<?> atualizarPessoa(@RequestBody Pessoa pessoa, @RequestHeader(value = "Authorization", required = true) String token) throws JSONException, IOException {

        return clienteService.atualizarCliente(pessoa, token);
    }

    @DeleteMapping("remover")
    public ResponseEntity<?> removerPessoa(@RequestHeader (value = "Authorization", required = true) String token) throws JSONException, IOException {
        return clienteService.removerPessoa(token);
    }



}
