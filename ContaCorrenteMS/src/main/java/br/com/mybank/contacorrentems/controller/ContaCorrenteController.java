package br.com.mybank.contacorrentems.controller;
import br.com.mybank.contacorrentems.domain.ContaCorrente;
import br.com.mybank.contacorrentems.domain.TransacoesEmCC;
import br.com.mybank.contacorrentems.service.ContaCorrenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacorrente")
@AllArgsConstructor
public class ContaCorrenteController {

    private final ContaCorrenteService contaCorrenteService;

    @PostMapping("adicionar")
    public ResponseEntity<?> adicionarContaCorrente(
        @RequestBody ContaCorrente contaCorrente, @RequestHeader (value = "Authorization", required = true) String token) {
        return contaCorrenteService.adicionarContaCorrente(contaCorrente, token);
    }

    @GetMapping("listartodas")
    public ResponseEntity<?> listarTodas(@RequestHeader (value = "Authorization", required = true) String token) {
        return contaCorrenteService.listarTodas(token);
    }

    //TODO restringir para que apenas os microserviços  consigam fazer essa consulta
    @GetMapping("listar/{id}")
    public Optional<ContaCorrente> listarPorIdUsuario(@PathVariable Integer id) {
        return contaCorrenteService.listarPorIdUsuario(id);
    }

    @PutMapping("atualizar")
    public ResponseEntity<?> atualizarContaCorrente(
            @RequestBody ContaCorrente contaCorrente, @RequestHeader (value = "Authorization", required = true) String token) {
        return contaCorrenteService.atualizarContacorrente(contaCorrente, token);
    }

    @DeleteMapping("remover")
    public ResponseEntity<?> removerContaCorrente(
            @RequestHeader (value = "Authorization", required = true) String token) {
        return contaCorrenteService.removerContaCorrente(token);
    }

    @PostMapping("depositar")
    public ResponseEntity<?> depositar (
            @RequestBody TransacoesEmCC deposito) {
        return contaCorrenteService.depositar(deposito);
    }

    @PostMapping("sacar")
    public ResponseEntity<?> sacar (@RequestBody TransacoesEmCC saque, @RequestHeader (value = "Authorization", required = true) String token) {
        return contaCorrenteService.sacar(saque, token);
    }

    @GetMapping("aplicarJuros/{percentual}")
    public void aplicarJuros (@PathVariable Float percentual) {
        contaCorrenteService.aplicarJuros(percentual);
    }

    @PostMapping("transferencia")
    public ResponseEntity<?> transferir (@RequestBody TransacoesEmCC transacoesEmCC, @RequestHeader (value = "Authorization", required = true) String token) {
       return contaCorrenteService.transferir(transacoesEmCC, token);
    }

    @PostMapping("recarga/{celular}/{valor}")
    public ResponseEntity<?> recarregarCelular (@PathVariable String celular, @PathVariable BigDecimal valor, @RequestHeader (value = "Authorization", required = true) String token) {
        return contaCorrenteService.recarregarCelular(celular, token, valor);
    }

}
