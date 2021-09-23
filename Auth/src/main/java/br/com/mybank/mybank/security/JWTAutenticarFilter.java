package br.com.mybank.mybank.security;

import br.com.mybank.mybank.data.DetalheUsuarioData;
import br.com.mybank.mybank.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@AllArgsConstructor
public class JWTAutenticarFilter extends UsernamePasswordAuthenticationFilter {

    public static final int TOKEN_EXPIRACAO = 600_000;
    //TODO esta senha deverá ser levada para uma variável de ambiente ou coisa do tipo
    public static final String TOKEN_SENHA = "75a8030c-83c3-41a4-9bc9-3a9c97a7d8db";
    private final AuthenticationManager authenticationManager;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
               usuario.getUsuario(), usuario.getSenha(), new ArrayList<>()
            ));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao autenticar usuário", e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        DetalheUsuarioData detalheUsuarioData = (DetalheUsuarioData) authResult.getPrincipal();
        String token = JWT.create()
                .withSubject(detalheUsuarioData.getUsername())
                .withClaim("id", detalheUsuarioData.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRACAO))
                .sign(Algorithm.HMAC512(TOKEN_SENHA));

        response.getWriter().write(token);
        response.getWriter().flush();

    }
}
