package com.SistemaPagamento.Exceptions;

import com.SistemaPagamento.DTOs.Output.GenericError;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Centralização do tratamento de exceções
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(NoSuchElementException ex){
        GenericError err = new GenericError("404", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex){
        GenericError err = new GenericError(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> nullPointerException(NullPointerException ex){
        GenericError err = new GenericError(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Insira o dado faltante corretamente: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> unauthorizedException(AuthorizationDeniedException ex){
        GenericError err = new GenericError(String.valueOf(HttpStatus.UNAUTHORIZED.value()), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    // metodo com resposta dinâmica
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> cantReadJson(HttpMessageNotReadableException ex){

        String resposta = "";

        String original = ex.getMessage();

        if(original.contains("Cannot deserialize value of type")){
            Pattern pattern = Pattern.compile("Cannot deserialize value of type `(.+?)` from (.+?):");
            Matcher matcher = pattern.matcher(original);

            String esperado = "";
            String valorFornecido = "";

            if(matcher.find()){
                esperado = matcher.group(1);
                valorFornecido = matcher.group(2);
            }

            String[] path = esperado.split("\\.");
            String extraido = path[path.length - 1];

            resposta = String.format("Valor fornecido: '%s' não é compatível com valor esperado: '%s'", valorFornecido, extraido).replace("\"", "");
        }else if(original.contains("not recognized as valid")){
            resposta = "O valor fornecido não foi reconhecido como válido";
        }else{
            resposta = "Erro no formato dos dados fornecidos";
        }

        GenericError err = new GenericError(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()), resposta);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(err);
    }
}
