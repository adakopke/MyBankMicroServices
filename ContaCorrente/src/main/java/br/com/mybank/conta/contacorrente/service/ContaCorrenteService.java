package br.com.mybank.conta.contacorrente.service;

import br.com.mybank.conta.contacorrente.domain.ContaCorrente;
import br.com.mybank.conta.contacorrente.repository.ContaCorrenteRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContaCorrenteService {

    private final ContaCorrenteRepository contaCorrenteRepository;

    public ResponseEntity<?> adicionarContaCorrente(ContaCorrente contaCorrente, String token) throws JSONException, IOException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));

        if (!isValidBearerToken(token) || !contaCorrente.getIdUsuario().equals(tokenJson.get("id"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(contaCorrenteRepository.save(contaCorrente));

    }

    public List<ContaCorrente> listarTodas() {
        return contaCorrenteRepository.findAll();
    }


    public Optional<ContaCorrente> listarPorIdUsuario(Integer id)  {
        return contaCorrenteRepository.findByIdUsuario(id);


    }

    public ResponseEntity<?> atualizarContacorrente(ContaCorrente contaCorrente, String token) throws JSONException, IOException {

        JSONObject tokenJson = new JSONObject(jwtFilter(token));

        if (!isValidBearerToken(token) || !contaCorrente.getIdUsuario().equals(tokenJson.get("id"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
        }

        contaCorrente.setId(listarPorIdUsuario((Integer) tokenJson.get("id")).get().getId());
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
