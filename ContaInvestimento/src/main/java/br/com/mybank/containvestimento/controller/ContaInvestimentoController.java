package br.com.mybank.containvestimento.controller;

import br.com.mybank.containvestimento.domain.ContaInvestimento;
import br.com.mybank.containvestimento.domain.TransacoesEmCI;
import br.com.mybank.containvestimento.service.ContaInvestimentoService;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/containvestimento")
@AllArgsConstructor
public class ContaInvestimentoController {

    private final ContaInvestimentoService contaInvestimentoService;

    @PostMapping("adicionar")
    public ResponseEntity<?> adicionarContaInvestimento(
            @RequestBody ContaInvestimento contaInvestimento, @RequestHeader (value = "Authorization", required = true) String token) throws JSONException, IOException, JSONException {
        return contaInvestimentoService.adicionarContaInvestimento(contaInvestimento, token);
    }

    @GetMapping("listartodas")
    public ResponseEntity<?> listarTodas(@RequestHeader (value = "Authorization", required = true) String token) throws JSONException, IOException {
        return contaInvestimentoService.listarTodas(token);
    }


    //TODO restringir para que apenas os microserviços  consigam fazer essa consulta
    @GetMapping("listar/{id}")
    public Optional<ContaInvestimento> listarPorIdUsuario(@PathVariable Integer id) {
        return contaInvestimentoService.listarPorIdUsuario(id);
    }

    @PutMapping("atualizar")
    public ResponseEntity<?> atualizarContaInvestimento(
            @RequestBody ContaInvestimento contaInvestimento, @RequestHeader (value = "Authorization", required = true) String token) throws JSONException, IOException {
        return contaInvestimentoService.atualizarInvestimento(contaInvestimento, token);
    }

    @DeleteMapping("remover")
    public ResponseEntity<?> removerContaCorrente(
            @RequestHeader (value = "Authorization", required = true) String token) throws JSONException, IOException {
        return contaInvestimentoService.removerContaInvestimento(token);
    }


    @PostMapping("aportar")
    public ResponseEntity<?> aportar(
            @RequestBody TransacoesEmCI aporte) {
        return contaInvestimentoService.aportar(aporte);

    }


    @GetMapping("aplicarJuros/{percentual}")
    public void aplicarJuros (@PathVariable Float percentual) {
        contaInvestimentoService.aplicarJuros(percentual);
    }

    @PostMapping("retirada")
    public ResponseEntity<?> retirada(@RequestBody TransacoesEmCI transacoesEmCI,
                                      @RequestHeader (value = "Authorization", required = true) String token
    ) throws JSONException, IOException {
       return contaInvestimentoService.retirada(transacoesEmCI, token);
    }

}
