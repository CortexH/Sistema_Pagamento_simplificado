package com.SistemaPagamento.Controller;

import com.SistemaPagamento.DTOs.Input.CancelDelayedTransactionDTO;
import com.SistemaPagamento.DTOs.Input.TransactionDTO;
import com.SistemaPagamento.DTOs.Output.GenericError;
import com.SistemaPagamento.DTOs.Output.GenericSuccessOutput;
import com.SistemaPagamento.Services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@Tag(name = "PrivateEndpoints", description = "Endpoints que necessitam de autenticação")
@Slf4j
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "Criar nova transação", description = "Cria nova transação e persiste no banco",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
    schema = @Schema(implementation = TransactionDTO.class), examples = {
            @ExampleObject(name = "Transação imediata", description = "Transação que acontece de forma imediata",
            value = """
                    {
                      "value" : 15,
                      "receiver" : "123-456-789-11",
                      "type" : "immediate"
                    }
                    """
            ),
            @ExampleObject(name = "Transação agendada", description = "Transação que acontece de forma agendada"
                    + " (transactionTime deverá ser menor que 1 mês, maior que o horário local e os minutos terão que ser múltiplos de 5",
            value = """
                    {
                      "value" : 15,
                      "receiver" : "123-456-789-11",
                      "type" : "delayed",
                      "transactionTime" : "2025-02-11T20:25:00"
                    }
                    """)
    })))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Resposta ao realizar a ação com sucesso",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericSuccessOutput.class,
                                    example =
                                            """
                                            {
                                                "timestamp": "2025-01-13T19:38:18.2337416",
                                                "status": 200,
                                                "message": "Transação realizada com sucesso!",
                                                "update": "new Transaction added"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Resposta para bad requests",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericError.class),
                    examples = {
                            @ExampleObject(description = "Mensagem ao não inserir document",
                            value = """
                                    {
                                        "status": "400",
                                        "message": "Usuário com document especificado não pôde ser encontrado"
                                    }
                                    """,
                                    name = "400 para document"
                            ),
                            @ExampleObject(description = "Mensagem ao inserir valor menor ou igual a 0 no campo 'value'",
                            value = """
                                    {
                                        "status": "400",
                                        "message": "O valor da transação deverá ser maior que 0"
                                    }
                                    """,
                                    name = "400 para value"
                            ),
                            @ExampleObject(description = "Resposta ao não inserir campo obrigatório",
                            value = """
                                    {
                                        "status": "400",
                                        "message": "Insira o dado faltante corretamente: {campo}"
                                    }
                                    """,
                                    name = "400 para campos gerais"
                            ),
                            @ExampleObject(description = "Resposta ao inserir data maior que o tempo permitido",
                            value = """
                                    {
                                        "status": "400",
                                        "message": "transactionTime maior do que o tempo permitido."
                                    }
                                    """,
                                    name = "400 para data > limite"
                            ),
                            @ExampleObject(description = "Resposta ao inserir data como menor que a data atual",
                            value = """
                                    {
                                        "status": "400",
                                        "message": "transactionTime inserida menor que a data atual."
                                    }
                                    """,
                                    name = "400 para data < LocalDateTime"
                            ),
                            @ExampleObject(description = "Resposta ao sender (usuário que enviou o dinheiro) não ter saldo o suficiente para realizar a transação",
                                    value = """
                                    {
                                        "status": "400",
                                        "message": "Sender não tem saldo suficiente."
                                    }
                                    """,
                                    name = "400 para saldo < value"
                            ),
                    })),
                    @ApiResponse(responseCode = "401", description = "Resposta ao não inserir ou inserir um token inválido",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericError.class,
                                    example =
                                            """
                                            {
                                                "status": "401",
                                                "message": "Token invalido"
                                            }
                                            """
                            )))
            }
    )
    @PostMapping("/new")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<?> newTransaction(@RequestBody TransactionDTO data, @RequestHeader HttpHeaders header) throws Exception {
        String token = header.getFirst("Authorization");
        return ResponseEntity.ok(transactionService.newTransaction(data, token));
    }

    @PostMapping("/cancel")
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<?> cancelDelayedTransaction(@RequestBody CancelDelayedTransactionDTO data, @RequestHeader HttpHeaders header){
        String token = header.getFirst("Authorization");
        return ResponseEntity.ok(transactionService.cancelTransaction(token, data));
    }
}
