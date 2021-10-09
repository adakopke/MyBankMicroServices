package br.com.mybank.emprestimoms.service;
import br.com.mybank.emprestimoms.config.ServersConfig;
import br.com.mybank.emprestimoms.domain.Emprestimo;
import br.com.mybank.emprestimoms.repository.EmprestimoRepository;
import br.com.mybank.emprestimoms.repository.LinhaDeCreditoRepository;
import br.com.mybank.emprestimoms.request.SolicitacaoEmprestimo;
import br.com.mybank.emprestimoms.response.TransacoesEmCC;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LinhaDeCreditoRepository linhaDeCreditoRepository;
    private final RestTemplate restTemplate;
    private final ServersConfig serversConfig;

    public ResponseEntity<?> solicitar(SolicitacaoEmprestimo emprestimoSolicitacao, String token) {
        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token) || !emprestimoSolicitacao.getIdUsuario().equals(tokenJson.get("id"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não existe linha de crédito liberada");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (emprestimoSolicitacao.getValorDoPrincipal().
                    compareTo(linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).get().getLinhaDeCredito()) > 0 ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Consulte seu gerente para liberação de crédito");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (emprestimoSolicitacao.getNumeroDeParcelas() >= linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).get().getParcelamentoMaximo()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Consulte seu gerente para avaliação do parcelamento");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setAtivo(true);
        try {
            emprestimo.setValorTotalEmprestimo(emprestimoSolicitacao.getValorDoPrincipal()
                            .add(emprestimoSolicitacao.getValorDoPrincipal()
                                  .multiply(linhaDeCreditoRepository.findByIdUsuario((Integer) tokenJson.get("id")).get().getTaxaLiberada())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        emprestimo.setSaldoDevedor(emprestimo.getValorTotalEmprestimo());
        emprestimo.setValorDoPrincipal(emprestimoSolicitacao.getValorDoPrincipal());
        emprestimo.setNumeroDeParcelas(emprestimoSolicitacao.getNumeroDeParcelas());

        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.getForEntity(
                    String.format("http://" + serversConfig.getContacorrente() + ":8082/api/contacorrente/listar/%s", tokenJson.get("id")), Map.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!response.getBody().get("numeroConta").equals(emprestimoSolicitacao.getContaDestino())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta de destino inválida");
        }

        TransacoesEmCC registroParaContaDestino = new TransacoesEmCC();
        registroParaContaDestino.setValor(emprestimo.getValorDoPrincipal());
        registroParaContaDestino.setContaDestino(emprestimoSolicitacao.getContaDestino());
        registroParaContaDestino.setData(emprestimo.getDataEmprestimo());
        registroParaContaDestino.setOperacoes("DEPOSITO");
        emprestimoRepository.save(emprestimo);
        restTemplate.postForEntity("http://" + serversConfig.getContacorrente() + ":8082/api/contacorrente/depositar", registroParaContaDestino, TransacoesEmCC.class);

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
