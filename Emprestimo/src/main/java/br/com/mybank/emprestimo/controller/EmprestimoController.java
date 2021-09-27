package br.com.mybank.emprestimo.controller;

import br.com.mybank.emprestimo.config.RestTemplateConfiguration;
import br.com.mybank.emprestimo.domain.Emprestimo;
import br.com.mybank.emprestimo.request.SolicitacaoEmprestimo;
import br.com.mybank.emprestimo.service.EmprestimoService;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/emprestimo")
@AllArgsConstructor

public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping("solicitar")
    public ResponseEntity<?> solicitarEmprestimo (@RequestBody SolicitacaoEmprestimo emprestimoSolicitacao, @RequestHeader (value = "Authorization", required = true) String token) throws JSONException, IOException {
        return emprestimoService.solicitar(emprestimoSolicitacao, token);
    }


}
