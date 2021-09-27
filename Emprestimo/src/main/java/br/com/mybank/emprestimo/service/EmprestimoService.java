package br.com.mybank.emprestimo.service;
import br.com.mybank.emprestimo.domain.LinhaDeCredito;
import br.com.mybank.emprestimo.repository.LinhaDeCreditoRepository;
import br.com.mybank.emprestimo.request.SolicitacaoEmprestimo;
import br.com.mybank.emprestimo.response.TransacoesEmCC;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import br.com.mybank.emprestimo.domain.Emprestimo;
import br.com.mybank.emprestimo.repository.EmprestimoRepository;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LinhaDeCreditoRepository linhaDeCreditoRepository;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> solicitar(SolicitacaoEmprestimo emprestimoSolicitacao, String token) throws JSONException, IOException {
        JSONObject tokenJson = new JSONObject(jwtFilter(token));
        if (!isValidBearerToken(token) || !emprestimoSolicitacao.getIdUsuario().equals(tokenJson.get("id"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        if (linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não existe linha de crédito liberada");
        }

        if (emprestimoSolicitacao.getValorDoPrincipal().
                compareTo(linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).get().getLinhaDeCredito()) > 0 ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Consulte seu gerente para liberação de crédito");
        }

        if (emprestimoSolicitacao.getNumeroDeParcelas() >= linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).get().getParcelamentoMaximo()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Consulte seu gerente para avaliação do parcelamento");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setAtivo(true);
        emprestimo.setValorTotalEmprestimo(emprestimoSolicitacao.getValorDoPrincipal()
                        .add(emprestimoSolicitacao.getValorDoPrincipal()
                              .multiply(linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).get().getTaxaLiberada())));
        emprestimo.setSaldoDevedor(emprestimo.getValorTotalEmprestimo());
        emprestimo.setValorDoPrincipal(emprestimoSolicitacao.getValorDoPrincipal());
        emprestimo.setNumeroDeParcelas(emprestimoSolicitacao.getNumeroDeParcelas());

        ResponseEntity<Map> response = restTemplate.getForEntity(
                String.format("http://localhost:8082/api/contacorrente/listar/%s", tokenJson.get("id")), Map.class);

        if (!response.getBody().get("numeroConta").equals(emprestimoSolicitacao.getContaDestino())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta de destino inválida");
        }

        TransacoesEmCC registroParaContaDestino = new TransacoesEmCC();
        registroParaContaDestino.setValor(emprestimo.getValorDoPrincipal());
        registroParaContaDestino.setContaDestino(emprestimoSolicitacao.getContaDestino());
        registroParaContaDestino.setData(emprestimo.getDataEmprestimo());
        registroParaContaDestino.setOperacoes("DEPOSITO");
        emprestimoRepository.save(emprestimo);
        restTemplate.postForEntity("http://localhost:8082/api/contacorrente/depositar", registroParaContaDestino, TransacoesEmCC.class);

        return ResponseEntity.status(HttpStatus.OK).body("Emprestimo realizado com sucesso");

        //TODO criar recurso para atualizar a linha de crédito liberada do cliente

    }

    private String jwtFilter(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        token.replace("Bearer", " ");
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
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
