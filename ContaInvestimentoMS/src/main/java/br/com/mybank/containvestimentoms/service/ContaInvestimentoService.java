package br.com.mybank.containvestimentoms.service;
import br.com.mybank.containvestimentoms.domain.ContaInvestimento;
import br.com.mybank.containvestimentoms.domain.Operacoes;
import br.com.mybank.containvestimentoms.domain.TransacoesEmCI;
import br.com.mybank.containvestimentoms.repository.ContaInvestimentoRepository;
import br.com.mybank.containvestimentoms.repository.TransacoesEmCIRepository;
import br.com.mybank.containvestimentoms.response.TransacoesEmCC;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContaInvestimentoService {

    private final ContaInvestimentoRepository contaInvestimentoRepository;
    private final TransacoesEmCIRepository transacoesEmCIRepository;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> adicionarContaInvestimento(ContaInvestimento contaInvestimento, String token)  {
        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token) || !contaInvestimento.getIdUsuario().equals(tokenJson.get("id"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(contaInvestimentoRepository.save(contaInvestimento));

    }

    public ResponseEntity<?> listarTodas(String token) {
        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token) || !tokenJson.get("sub").equals("admin")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body(contaInvestimentoRepository.findAll());
    }

    public Optional<ContaInvestimento> listarPorIdUsuario(Integer id) {
        return contaInvestimentoRepository.findByIdUsuario(id);
    }

    public Optional<ContaInvestimento> listarPorIdConta(Integer id) {
        return contaInvestimentoRepository.findById(id);
    }

    public Optional<ContaInvestimento> listarPorNumeroConta(String id) {
        return contaInvestimentoRepository.findByNumeroConta(id);
    }

    public ResponseEntity<?> atualizarInvestimento(ContaInvestimento contaInvestimento, String token) {

        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token) || !tokenJson.get("sub").equals("admin")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        contaInvestimento.setId(listarPorIdUsuario(contaInvestimento.getIdUsuario()).get().getId());
        return ResponseEntity.status(HttpStatus.OK).body(contaInvestimentoRepository.save(contaInvestimento));
    }


    public ResponseEntity<?> removerContaInvestimento(String token) {

        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContaInvestimento contaInvestimento = null;
        try {
            contaInvestimento = listarPorIdUsuario((Integer) tokenJson.get("id")).get();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!contaInvestimento.isAtivo()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta já está desativada");
        }

        if (contaInvestimento.getSaldoContaInvestimento().compareTo(BigDecimal.valueOf(0)) > 0 ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta investimento possui saldo");
        }

        contaInvestimento.setAtivo(false);
        return ResponseEntity.status(HttpStatus.OK).body(contaInvestimentoRepository.save(contaInvestimento));
    }

    public ResponseEntity<?> aportar(TransacoesEmCI aporte) {

        Optional<ContaInvestimento> contaInvestimentoOptional = contaInvestimentoRepository.findByNumeroConta(aporte.getContaDestino());
        BigDecimal saldoCI = contaInvestimentoOptional.get().getSaldoContaInvestimento();
        contaInvestimentoOptional.get().setSaldoContaInvestimento(saldoCI.add(aporte.getValor()));

        ContaInvestimento contaInvestimento = CIOptionalParaCI(contaInvestimentoOptional);
        contaInvestimentoRepository.save(contaInvestimento);
        transacoesEmCIRepository.save(aporte);

        return ResponseEntity.status(HttpStatus.OK).body("{ \"mensagem\" : \"Aporte realizado com sucesso!\"}");

    }




    private String jwtFilter(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        token.replace("Bearer", " ");
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
    }

    public ResponseEntity<?> retirada(TransacoesEmCI retirada, String token) {
        JSONObject tokenJson = null;
        try {
            tokenJson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token) || ! listarPorNumeroConta(retirada.getContaOrigem()).get().getIdUsuario().equals(tokenJson.get("id"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.getForEntity(
            String.format("http://localhost:8082/api/contacorrente/listar/%s", tokenJson.get("id")), Map.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!response.getBody().get("numeroConta").equals(retirada.getContaDestino())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta de destino inválida");
        }

        Optional<ContaInvestimento> contaInvestimentoOptional = null;
        try {
            contaInvestimentoOptional = listarPorIdUsuario((Integer) tokenJson.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BigDecimal saldoCI = contaInvestimentoOptional.get().getSaldoContaInvestimento();
        BigDecimal transferenciaValor = retirada.getValor();

        if (transferenciaValor.compareTo(saldoCI) > 0 ) {
            return ResponseEntity.status(HttpStatus.OK).body("Saldo insuficiente!");
        }
        contaInvestimentoOptional.get().setSaldoContaInvestimento(saldoCI.subtract(transferenciaValor));
        ContaInvestimento contaInvestimento = CIOptionalParaCI(contaInvestimentoOptional);
        contaInvestimentoRepository.save(contaInvestimento);
        transacoesEmCIRepository.save(retirada);

        //TODO Perguntar sobre como garantir que caso o serviço de conta corrente não esteja funcionando, aplique o rollback

        TransacoesEmCC registroParaContaDestino = new TransacoesEmCC();
        registroParaContaDestino.setValor(retirada.getValor());
        registroParaContaDestino.setContaDestino(retirada.getContaDestino());
        registroParaContaDestino.setData(retirada.getData());
        registroParaContaDestino.setOperacoes(Operacoes.DEPOSITO);
        registroParaContaDestino.setContaOrigem(String.valueOf(contaInvestimentoOptional.get().getNumeroConta()));

        restTemplate.postForEntity("http://localhost:8082/api/contacorrente/depositar", registroParaContaDestino, TransacoesEmCC.class);

        return ResponseEntity.status(HttpStatus.OK).body("Retirada realizada com sucesso");

    }

    private ContaInvestimento CIOptionalParaCI(Optional<ContaInvestimento> contaInvestimentoOptional) {
        ContaInvestimento contaInvestimento = new ContaInvestimento();
        contaInvestimento.setId(contaInvestimentoOptional.get().getId());
        contaInvestimento.setSaldoContaInvestimento(contaInvestimentoOptional.get().getSaldoContaInvestimento());
        contaInvestimento.setAtivo(contaInvestimentoOptional.get().isAtivo());
        contaInvestimento.setNumeroConta(contaInvestimentoOptional.get().getNumeroConta());
        contaInvestimento.setDataAbertura(contaInvestimentoOptional.get().getDataAbertura());
        contaInvestimento.setDataFechamento(contaInvestimentoOptional.get().getDataFechamento());
        contaInvestimento.setIdPessoa(contaInvestimentoOptional.get().getIdPessoa());
        contaInvestimento.setIdUsuario(contaInvestimentoOptional.get().getIdUsuario());
        return contaInvestimento;
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


    public void aplicarJuros(Float percentual) {

        List<ContaInvestimento> contaInvestimentoList = contaInvestimentoRepository.findAll();

        for (ContaInvestimento contaInvestimento : contaInvestimentoList) {

            if (contaInvestimento.getSaldoContaInvestimento().compareTo(BigDecimal.valueOf(0)) > 0) {
                BigDecimal saldoInvestimento = contaInvestimento.getSaldoContaInvestimento();
                contaInvestimento.setSaldoContaInvestimento(
                        contaInvestimento.getSaldoContaInvestimento()
                                .add(contaInvestimento.getSaldoContaInvestimento()
                                        .multiply(BigDecimal.valueOf(percentual))));
                TransacoesEmCI juros = new TransacoesEmCI();
                juros.setOperacoes(Operacoes.JUROS);
                juros.setData(LocalDate.now());
                juros.setValor(contaInvestimento.getSaldoContaInvestimento().subtract(saldoInvestimento));
                transacoesEmCIRepository.save(juros);
                contaInvestimentoRepository.save(contaInvestimento);
            }
        }
    }

}
