package br.com.mybank.emprestimoms.controller;

import br.com.mybank.emprestimoms.request.SolicitacaoEmprestimo;
import br.com.mybank.emprestimoms.service.EmprestimoService;
import br.com.mybank.emprestimoms.service.EmprestimoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/emprestimo")
@AllArgsConstructor

public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping("solicitar")
    public ResponseEntity<?> solicitarEmprestimo (@RequestBody SolicitacaoEmprestimo emprestimoSolicitacao, @RequestHeader (value = "Authorization", required = true) String token) {
        return emprestimoService.solicitar(emprestimoSolicitacao, token);
    }


}
