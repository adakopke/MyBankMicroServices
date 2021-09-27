package br.com.mybank.conta.contacorrente.service;

import br.com.mybank.conta.contacorrente.domain.ContaCorrente;
import br.com.mybank.conta.contacorrente.domain.Operacoes;
import br.com.mybank.conta.contacorrente.domain.TransacoesEmCC;
import br.com.mybank.conta.contacorrente.repository.ContaCorrenteRepository;
import br.com.mybank.conta.contacorrente.repository.TransacoesEmCCRepository;
import br.com.mybank.conta.contacorrente.response.TransacoesEmCI;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
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
public class ContaCorrenteService {

    private final ContaCorrenteRepository contaCorrenteRepository;
    private final TransacoesEmCCRepository transacoesEmCCRepository;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> adicionarContaCorrente(ContaCorrente contaCorrente, String token) throws JSONException, IOException {
        JSONObject tokenJson = new JSONObject(jwtFilter(token));
        if (!isValidBearerToken(token) || !contaCorrente.getIdUsuario().equals(tokenJson.get("id"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }
            return ResponseEntity.status(HttpStatus.CREATED).body(contaCorrenteRepository.save(contaCorrente));
    }

    public ResponseEntity<?> listarTodas(String token) throws JSONException, IOException {
        JSONObject tokenJson = new JSONObject(jwtFilter(token));
        if (!isValidBearerToken(token) || !tokenJson.get("sub").equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        return ResponseEntity.status(HttpStatus.OK).body(contaCorrenteRepository.findAll());

    }

    public Optional<ContaCorrente> listarPorIdUsuario(Integer id) {
        return contaCorrenteRepository.findByIdUsuario(id);
    }

    public Optional<ContaCorrente> listarPorIdConta(Integer id) {
        return contaCorrenteRepository.findById(id);
    }

    public ResponseEntity<?> atualizarContacorrente(ContaCorrente contaCorrente, String token) throws JSONException, IOException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));
        if (!isValidBearerToken(token) || !tokenJson.get("sub").equals("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        contaCorrente.setId(listarPorIdUsuario(contaCorrente.getIdUsuario()).get().getId());
        return ResponseEntity.status(HttpStatus.OK).body(contaCorrenteRepository.save(contaCorrente));

    }

    public ResponseEntity<?> removerContaCorrente(String token) throws JSONException, IOException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));
        if (!isValidBearerToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        ContaCorrente contaCorrente = listarPorIdUsuario((Integer) tokenJson.get("id")).get();

        if (!contaCorrente.isAtivo()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta já está desativada");
        }

        if (contaCorrente.getSaldoCorrente().compareTo(BigDecimal.valueOf(0)) > 0 ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta corrente possui saldo");
        }

        if (contaCorrente.getSaldoEspecial().compareTo(BigDecimal.valueOf(0)) > 0 ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta corrente está utilizando o especial");
        }

        contaCorrente.setAtivo(false);
        return ResponseEntity.status(HttpStatus.OK).body(contaCorrenteRepository.save(contaCorrente));
    }


    private String jwtFilter(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        token.replace("Bearer", " ");
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        return payload;
    }


    public ResponseEntity<?> depositar(TransacoesEmCC deposito) {

        Optional<ContaCorrente> contaCorrenteOptional = contaCorrenteRepository.findByNumeroConta(deposito.getContaDestino());
        BigDecimal saldoCC = contaCorrenteOptional.get().getSaldoCorrente();
        BigDecimal saldoEspecial = contaCorrenteOptional.get().getSaldoEspecial();
        if (saldoEspecial.equals(BigDecimal.valueOf(0))) {
            contaCorrenteOptional.get().setSaldoCorrente(saldoCC.add(deposito.getValor()));
        } else if (saldoEspecial.compareTo(deposito.getValor()) >= 0) {
            contaCorrenteOptional.get().setSaldoEspecial(saldoEspecial.subtract(deposito.getValor()));
        } else {
            contaCorrenteOptional.get().setSaldoCorrente(saldoCC.add(deposito.getValor().subtract(saldoEspecial)));
            contaCorrenteOptional.get().setSaldoEspecial(BigDecimal.valueOf(0));
            contaCorrenteOptional.get().setDataInicioUsoEspecial(null);
        }

        ContaCorrente contaCorrente = CCOptionalParaCC(contaCorrenteOptional);
        contaCorrenteRepository.save(contaCorrente);
        transacoesEmCCRepository.save(deposito);

        return ResponseEntity.status(HttpStatus.OK).body("Depósito realizado com sucesso!");

    }

    public ResponseEntity<?> sacar(TransacoesEmCC saque, String token) throws JSONException, IOException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));

        if (!isValidBearerToken(token) || ! listarPorIdConta(saque.getIdConta()).get().getIdUsuario().equals(tokenJson.get("id"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        Optional<ContaCorrente> contaCorrenteOptional = listarPorIdUsuario((Integer) tokenJson.get("id"));
        BigDecimal saldoCC = contaCorrenteOptional.get().getSaldoCorrente();
        BigDecimal saldoEspecial = contaCorrenteOptional.get().getSaldoEspecial();
        BigDecimal saqueValor = saque.getValor();

        if (saqueValor.compareTo(saldoCC) <= 0 ) {
            contaCorrenteOptional.get().setSaldoCorrente(saldoCC.subtract(saqueValor));
        } else if (saqueValor.compareTo(saldoCC) > 0
                && saqueValor.add(saldoEspecial).compareTo(contaCorrenteOptional.get().getLimiteEspecial()) <= 0)  {
            contaCorrenteOptional.get().setSaldoEspecial(saldoEspecial.add(saqueValor.subtract(saldoCC)));
            contaCorrenteOptional.get().setSaldoCorrente(BigDecimal.valueOf(0));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Saldo insuficiente!");
        }

        ContaCorrente contaCorrente = CCOptionalParaCC(contaCorrenteOptional);
        contaCorrenteRepository.save(contaCorrente);
        transacoesEmCCRepository.save(saque);

        return ResponseEntity.status(HttpStatus.OK).body("Saque realizado com sucesso!");
    }

    public ResponseEntity<?> transferir(TransacoesEmCC transferencia, String token) throws JSONException, IOException {


        JSONObject tokenJson = new JSONObject(jwtFilter(token));

        if (!isValidBearerToken(token) || !listarPorIdConta(transferencia.getIdConta()).get().getIdUsuario().equals(tokenJson.get("id"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }


        if (transferencia.getOperacoes().equals(Operacoes.PIX)) {

            ResponseEntity<Map> response = restTemplate.getForEntity(
                    String.format("http://localhost:8079/api/pix/consultar/%s", transferencia.getPixDestino()), Map.class);
//TODO tratar essa resposta de uma maneira melhor, tentar pelo código do retorno fornecido pelo pixAPI
        if (response.getBody().get("resp").equals("false")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chave pix não encontrada");
            }
        }

        Optional<ContaCorrente> contaCorrenteOptional = listarPorIdUsuario((Integer) tokenJson.get("id"));
        BigDecimal saldoCC = contaCorrenteOptional.get().getSaldoCorrente();
        BigDecimal saldoEspecial = contaCorrenteOptional.get().getSaldoEspecial();
        BigDecimal transferenciaValor = transferencia.getValor();

        if (transferenciaValor.compareTo(saldoCC) <= 0 ) {
            contaCorrenteOptional.get().setSaldoCorrente(saldoCC.subtract(transferenciaValor));
        } else if (transferenciaValor.compareTo(saldoCC) > 0
                && transferenciaValor.add(saldoEspecial).compareTo(contaCorrenteOptional.get().getLimiteEspecial()) <= 0)  {
            contaCorrenteOptional.get().setSaldoEspecial(saldoEspecial.add(transferenciaValor.subtract(saldoCC)));
            contaCorrenteOptional.get().setSaldoCorrente(BigDecimal.valueOf(0));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Saldo insuficiente!");
        }

        ContaCorrente contaCorrente = CCOptionalParaCC(contaCorrenteOptional);
        contaCorrenteRepository.save(contaCorrente);
        transacoesEmCCRepository.save(transferencia);

        if (transferencia.getOperacoes().equals(Operacoes.APORTE)) {

            if (!contaCorrenteRepository.findByNumeroConta(transferencia.getContaOrigem()).get().getIdUsuario().equals(tokenJson.get("id"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta de origem não permitida");
            }

            ResponseEntity<Map> response = restTemplate.getForEntity(
                    String.format("http://localhost:8083/api/containvestimento/listar/%s", tokenJson.get("id")), Map.class);

            if (!response.getBody().get("numeroConta").equals(transferencia.getContaDestino())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Conta de destino inválida");
            }

            TransacoesEmCI registroParaContaDestino = new TransacoesEmCI();
            registroParaContaDestino.setValor(transferencia.getValor());
            registroParaContaDestino.setContaDestino(transferencia.getContaDestino());
            registroParaContaDestino.setData(transferencia.getData());
            registroParaContaDestino.setOperacoes(Operacoes.APORTE);
            registroParaContaDestino.setContaOrigem(String.valueOf(transferencia.getContaOrigem()));
            restTemplate.postForEntity("http://localhost:8083/api/containvestimento/aportar", registroParaContaDestino, TransacoesEmCI.class);

            return ResponseEntity.status(HttpStatus.OK).body("Aporte realizado com sucesso");

        } else {

            Optional<ContaCorrente> contadestino = contaCorrenteRepository.findByNumeroConta(transferencia.getContaDestino());
            BigDecimal saldoCCDestino = contadestino.get().getSaldoCorrente();
            BigDecimal saldoEspecialDestino = contadestino.get().getSaldoEspecial();
            if (saldoEspecialDestino.equals(BigDecimal.valueOf(0))) {
                contadestino.get().setSaldoCorrente(saldoCCDestino.add(transferencia.getValor()));
            } else if (saldoEspecialDestino.compareTo(transferencia.getValor()) >= 0) {
                contadestino.get().setSaldoEspecial(saldoEspecialDestino.subtract(transferencia.getValor()));
            } else {
                contadestino.get().setSaldoCorrente(saldoCCDestino.add(transferencia.getValor().subtract(saldoEspecialDestino)));
                contadestino.get().setSaldoEspecial(BigDecimal.valueOf(0));
                contadestino.get().setDataInicioUsoEspecial(null);
            }

            ContaCorrente contaCorrenteDestino = CCOptionalParaCC(contadestino);
            contaCorrenteRepository.save(contaCorrenteDestino);
            TransacoesEmCC registroParaContaDestino = new TransacoesEmCC();
            registroParaContaDestino.setIdConta(contaCorrenteDestino.getId());
            registroParaContaDestino.setValor(transferencia.getValor());
            registroParaContaDestino.setContaDestino(transferencia.getContaDestino());
            registroParaContaDestino.setData(transferencia.getData());
            registroParaContaDestino.setOperacoes(transferencia.getOperacoes());
            registroParaContaDestino.setContaOrigem(String.valueOf(transferencia.getIdConta()));
            transacoesEmCCRepository.save(registroParaContaDestino);

            if (transferencia.getOperacoes().equals(Operacoes.PIX)) {
                return ResponseEntity.status(HttpStatus.OK).body("Pix realizado com sucesso!");
            }

            if (transferencia.getOperacoes().equals(Operacoes.DOC)) {
                return ResponseEntity.status(HttpStatus.OK).body("DOC realizado com sucesso!");
            }

            return ResponseEntity.status(HttpStatus.OK).body("Transferência realizada com sucesso!");
        }

    }

    private ContaCorrente CCOptionalParaCC(Optional<ContaCorrente> contaCorrenteOptional) {
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setId(contaCorrenteOptional.get().getId());
        contaCorrente.setSaldoCorrente(contaCorrenteOptional.get().getSaldoCorrente());
        contaCorrente.setSaldoEspecial(contaCorrenteOptional.get().getSaldoEspecial());
        contaCorrente.setAtivo(contaCorrenteOptional.get().isAtivo());
        contaCorrente.setDataInicioUsoEspecial(contaCorrenteOptional.get().getDataInicioUsoEspecial());
        contaCorrente.setNumeroConta(contaCorrenteOptional.get().getNumeroConta());
        contaCorrente.setDataAbertura(contaCorrenteOptional.get().getDataAbertura());
        contaCorrente.setDataFechamento(contaCorrenteOptional.get().getDataFechamento());
        contaCorrente.setIdPessoa(contaCorrenteOptional.get().getIdPessoa());
        contaCorrente.setIdUsuario(contaCorrenteOptional.get().getIdUsuario());
        contaCorrente.setLimiteEspecial(contaCorrenteOptional.get().getLimiteEspecial());
        return contaCorrente;
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

        List<ContaCorrente> contaCorrenteList = contaCorrenteRepository.findAll();

        for (ContaCorrente contaCorrente : contaCorrenteList) {

            if (contaCorrente.getSaldoEspecial().compareTo(BigDecimal.valueOf(0)) > 0) {
                BigDecimal saldoEspecial = contaCorrente.getSaldoEspecial();
                contaCorrente.setSaldoEspecial(
                        contaCorrente.getSaldoEspecial()
                                .add(contaCorrente.getSaldoEspecial()
                                        .multiply(BigDecimal.valueOf(percentual))));
                TransacoesEmCC juros = new TransacoesEmCC();
                juros.setOperacoes(Operacoes.JUROS);
                juros.setData(LocalDate.now());
                juros.setIdConta(contaCorrente.getId());
                juros.setValor(contaCorrente.getSaldoEspecial().subtract(saldoEspecial));
                transacoesEmCCRepository.save(juros);
                contaCorrenteRepository.save(contaCorrente);
            }
        }
    }


    public ResponseEntity<?> recarregarCelular(String celular, String token, BigDecimal valor) throws JSONException, IOException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));
        if (!isValidBearerToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

           ResponseEntity<Map> response = restTemplate.getForEntity(
                String.format("http://localhost:8081/api/cliente/pesquisar/%s", tokenJson.get("id")), Map.class);

            if (!response.getBody().get("telefone").equals(celular)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Telefone não corresponde com o cadastrado");
        }

        ContaCorrente contaCorrente = listarPorIdUsuario((Integer) tokenJson.get("id")).get();
        BigDecimal saldoCC = contaCorrente.getSaldoCorrente();
        BigDecimal saldoEspecial = contaCorrente.getSaldoEspecial();

        if (valor.compareTo(saldoCC) <= 0 ) {
            contaCorrente.setSaldoCorrente(saldoCC.subtract(valor));
        } else if (valor.compareTo(saldoCC) > 0
                && valor.add(saldoEspecial).compareTo(contaCorrente.getLimiteEspecial()) <= 0)  {
            contaCorrente.setSaldoEspecial(saldoEspecial.add(valor.subtract(saldoCC)));
            contaCorrente.setSaldoCorrente(BigDecimal.valueOf(0));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Saldo insuficiente!");
        }

        contaCorrenteRepository.save(contaCorrente);
        TransacoesEmCC recarga = new TransacoesEmCC();
        recarga.setOperacoes(Operacoes.RECARGACEL);
        recarga.setData(LocalDate.now());
        recarga.setIdConta(contaCorrente.getId());
        recarga.setValor(valor);
        transacoesEmCCRepository.save(recarga);

        return ResponseEntity.status(HttpStatus.OK).body("Recarga realizada com sucesso!");
    }
}
