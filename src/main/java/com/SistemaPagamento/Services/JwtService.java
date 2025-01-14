package com.SistemaPagamento.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class JwtService {

    private static final String secretKey = "Chave Muito, MUITO secreta. Pode acreditar, eu não mentiria :)"; // chave secreta
    private static final String issuer = "Sistema_Pagamento"; // emissor
    private static final Algorithm algorithm = Algorithm.HMAC256(secretKey);

    private static final Integer tokenMaxLifeDays = 30;

    // metodo para gerar o token
    public String generateToken(String document){
        return JWT.create() // criar token JWT
                .withIssuer(issuer) // definir o emissor do token
                .withSubject(document) // definir o "corpo" do token
                .withIssuedAt(creationDate()) // definir data de criação
                .withExpiresAt(expirationDate()) // definir data de expiração
                .sign(algorithm); // assinar com algorítmo

    }

    // metodo para retornar o subject do token
    public String getTokenSubject(String token){
        try{
            // se o token for nulo, lança exceção
            if(token == null) throw new JWTVerificationException("Token invalido");

            // se o token inicia com 'Bearer ', então corte o início
            if(token.startsWith("Bearer ")){
                token = token.substring(7);
            }

            // retorna o subject do token com sucesso.
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            // captura exceção relacionada ao token e lança exceção de autenticação negada
            throw new AuthorizationDeniedException(e.getMessage());
        }

    }

    public Boolean validadeToken(String token, String document){
        String subject = JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getSubject();

        return subject.equals(document);
    }

    // metodo para retornar token por http
    public String getTokenFromHttp(HttpServletRequest http){
        String header = http.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            return header.substring(7);
        }
        return null;
    }

    // metodo para retornar data de criação do token
    public Instant creationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    // metodo para retornar data de expiração do token
    public Instant expirationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusDays(tokenMaxLifeDays).toInstant();
    }

}
