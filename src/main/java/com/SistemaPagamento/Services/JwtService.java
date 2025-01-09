package com.SistemaPagamento.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.*;

@Service
public class JwtService {

    private static final String secretKey = "Chave Muito, MUITO secreta. Pode acreditar, eu não mentiria :)"; // chave secreta
    private static final String issuer = "Sistema_Pagamento"; // emissor
    private static final Algorithm algorithm = Algorithm.HMAC256(secretKey);

    public String generateToken(String document){
        try{
            return JWT.create() // criar token JWT
                    .withIssuer(issuer) // definir o emissor do token
                    .withSubject(document) // definir o "corpo" do token
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm); // assinar com algorítmo

        } catch (RuntimeException e) {
            throw new RuntimeException(/*"Falha ao autenticar. Tente novamente mais tarde."*/);
        }
    }

    public String getTokenSubject(String token){
        try {
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (Exception e){
            throw new RuntimeException("Falha ao autenticar. Tente novamente mais tarde.");
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

    public Instant creationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    public Instant expirationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusDays(30).toInstant();
    }

}
