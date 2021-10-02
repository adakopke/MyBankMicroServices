package br.com.mybank.cliente.cliente.service;

import br.com.mybank.cliente.cliente.domain.Pessoa;
import br.com.mybank.cliente.cliente.dto.EmailDTO;
import br.com.mybank.cliente.cliente.repository.ClienteRepository;
import com.auth0.jwt.*;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClienteService {

        private final ClienteRepository clienteRepository;
        private final RestTemplate restTemplate;
        private final RabbitMQService rabbitMQService;


        public ResponseEntity<?> adicionarCliente(Pessoa pessoa, String token) {


            JSONObject tokenJson = null;
            try {
                tokenJson = new JSONObject(jwtFilter(token));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (!isValidBearerToken(token) || !pessoa.getIdUsuario().equals(tokenJson.get("id"))) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            EmailDTO mensagem = new EmailDTO();

            mensagem.setOwnerRef("Anderson");
            mensagem.setEmailFrom("kopkeanderson@gmail.com");
            mensagem.setEmailTo("adakopke@gmail.com");
            mensagem.setSubject("produtor");
            mensagem.setText("Envio via produtor");

            rabbitMQService.enviaMensagem("ms-mail", mensagem);

            return ResponseEntity.status(HttpStatus.CREATED).body(clienteRepository.save(pessoa));


    }

   public ResponseEntity<?> listarTodos(String token)  {

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

       return ResponseEntity.status(HttpStatus.OK).body(clienteRepository.findAll());

    }

    public ResponseEntity<?> atualizarCliente(Pessoa pessoa, String token) {

        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Necessário informar o token");
        }

        JSONObject tokenjson = null;
        try {
            tokenjson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (!isValidBearerToken(token) || !pessoa.getIdUsuario().equals(tokenjson.get("id"))) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou inválido");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            pessoa.setId(listarPorIdUsuario((Integer) tokenjson.get("id")).get().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).body(clienteRepository.save(pessoa));
    }


    //TODO verificar se tem conta ativa antes de deixar desativar o cliente
    public ResponseEntity<?> removerPessoa(String token) {

        JSONObject tokenjson = null;
        try {
            tokenjson = new JSONObject(jwtFilter(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!isValidBearerToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
            } else if (listarPorIdUsuario((Integer) tokenjson.get("id")).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Pessoa pessoa = null;
        try {
            pessoa = listarPorIdUsuario((Integer) tokenjson.get("id")).get();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pessoa.setAtivo(false);
       clienteRepository.save(pessoa);
       return ResponseEntity.status(HttpStatus.OK).body("Usuário desativado com sucesso");
       }

    public Optional<Pessoa> listarPorIdUsuario(Integer id) {
        return clienteRepository.findByIdUsuario(id);
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


