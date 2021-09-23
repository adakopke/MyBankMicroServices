package br.com.mybank.conta.contacorrente.controller;


import br.com.mybank.conta.contacorrente.domain.ContaCorrente;
import br.com.mybank.conta.contacorrente.service.ContaCorrenteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacorrente")
@AllArgsConstructor
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    @PostMapping("adicionar")
    public ContaCorrente adicionarContaCorrente(@RequestBody ContaCorrente contaCorrente) {
        return contaCorrenteService.adicionarContaCorrente(contaCorrente);
    }

    @GetMapping("listartodas")
    public List<ContaCorrente>  listarTodas() {
        return contaCorrenteService.listarTodas();
    }

    @GetMapping("listar/{id}")
    public Optional<ContaCorrente> listarPorId(@PathVariable Integer id) {
        return contaCorrenteService.listarPorId(id);
    }

    @PutMapping("atualizar/{id}")
    public ContaCorrente atualizarContaCorrente(@PathVariable Integer id, @RequestBody ContaCorrente contaCorrente) {
        return contaCorrenteService.atualizarContacorrente(id, contaCorrente);
    }

}
