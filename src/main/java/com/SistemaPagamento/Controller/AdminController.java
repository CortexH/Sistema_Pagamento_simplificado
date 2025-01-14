package com.SistemaPagamento.Controller;

import com.SistemaPagamento.DTOs.Input.ChangeUserBalanceDTO;
import com.SistemaPagamento.DTOs.Input.ChangeUserRoleDTO;
import com.SistemaPagamento.DTOs.Output.GenericError;
import com.SistemaPagamento.DTOs.Output.GenericSuccessOutput;
import com.SistemaPagamento.Domain.Transaction.Transaction;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Services.TransactionService;
import com.SistemaPagamento.Services.UserBalanceService;
import com.SistemaPagamento.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin controller", description = "Endpoints criados para devs realizarem testes e debugs.")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceService userBalanceService;
    @Autowired
    private TransactionService transactionService;

    @Operation(security = @SecurityRequirement(name = "jwt_auth"), summary = "Get all users", description = "Endpoint para trazer todos os usuários e seus dados",
            responses = {@ApiResponse(responseCode = "200", description = "Retorna todos os usuários",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class, examples = {
                            """
                            [
                                {
                                    "id": 1,
                                    "firstName": "Ronaldinho",
                                    "lastName": "Gaucho",
                                    "password": "12345",
                                    "email": "ronaldo@example.com",
                                    "document": "123-456-789-10",
                                    "classification": "Common",
                                    "blocked": false,
                                    "deleted": false,
                                    "balance": 0.00,
                                    "role": "ADMIN"
                                }
                            ]
                            """
                    }))), @ApiResponse(responseCode = "401", description = "Resposta ao inserir token inválido ou deixálo vazio",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericError.class, examples = {
                    """
                    {
                        "status": "401",
                        "message": "Token inválido"
                    }
                    """
            }))),
                    @ApiResponse(responseCode = "403", description = "Resposta ao usuário não ter permissão para acessar o conteúdo",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericSuccessOutput.class,
                                    example =
                                            """
                                            {
                                                "status": "403",
                                                "message": "O ação que você tentou realizar é proibida"
                                            }
                                            """
                            )))}
   )
    @GetMapping("/getusers")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.returnAllUsers());
    }

    @Operation(summary = "Change user balance", description = "Altera o saldo do usuário e cria um log com essa atualização no banco", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = {
            @Content(schema = @Schema(implementation = ChangeUserBalanceDTO.class), examples = {
                    @ExampleObject(name = "operação de adição", description = "Atualiza o saldo com o cálculo: saldo + inputValue",
                    value = """
                            {
                                "balanceOperation" : "PLUS",
                                "inputValue" : 10000,
                                "reason" : "e.g verificar token de autorização",
                                "adminId" : 2,
                                "userId" : 2
                            }
                            """),
                    @ExampleObject(name = "operação de subtração", description = "Atualiza o saldo com o cálculo: saldo - inputValue",
                    value = """
                            {
                                "balanceOperation" : "MINUS",
                                "inputValue" : 10000,
                                "reason" : "e.g verificar token de autorização",
                                "adminId" : 2,
                                "userId" : 2
                            }
                            """),
                    @ExampleObject(name = "operação de modificação", description = "Atualiza o saldo com o cálculo: saldo = inputValue",
                            value = """
                            {
                                "balanceOperation" : "SET",
                                "inputValue" : 10000,
                                "reason" : "e.g verificar token de autorização",
                                "adminId" : 2,
                                "userId" : 2
                            }
                            """)
            })
    }), responses = {
            @ApiResponse(responseCode = "200", description = "Resposta ao inserir corretamente os dados", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = GenericSuccessOutput.class, examples = {
                    """
                    {
                        "timestamp": "2025-01-12T21:26:08.9753146",
                        "status": 200,
                        "message": "Saldo do usuário Cristiano Ronaldo atualizado com sucesso!",
                        "update": "Saldo: 0.00 -> 10000.00"
                    }
                    """
            }))),
            @ApiResponse(responseCode = "401", description = "Resposta ao inserir token inválido ou deixá-lo vazio", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GenericSuccessOutput.class, examples = {
                            """
                            {
                                "status": "401",
                                "message": "Token invalido ou vazio."
                            }
                            """
                    }))),
            @ApiResponse(responseCode = "403", description = "Resposta ao usuário não ter permissão para acessar o conteúdo",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericSuccessOutput.class,
                            example =
                                    """
                                    {
                                        "status": "403",
                                        "message": "O ação que você tentou realizar é proibida"
                                    }
                                    """
                    )))
    })
    @PatchMapping("/SetUserBalance")
    public ResponseEntity<?> setUserBalance(@RequestBody ChangeUserBalanceDTO data){
        return ResponseEntity.ok(userBalanceService.changeUserBalance(data));
    }


    @Operation(
            security = @SecurityRequirement(name = "jwt_auth"),
            summary = "Get all transactions",
            description = "Endpoint para retornar todas as transações e suas informações",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Retornar com sucesso uma ou mais transações",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class,
                                    examples = {
                                            """
                                                [
                                                    {
                                                        "id": 1,
                                                        "type": "delayed",
                                                        "state": "delayed",
                                                        "transactionValue": 15.00,
                                                        "senderDocument": "123-456-789-12",
                                                        "receiverDocument": "123-456-789-11",
                                                        "transactionTime": "2025-02-11 20:25:00"
                                                    },
                                                    {
                                                        "id": 2,
                                                        "type": "immediate",
                                                        "state": "completed",
                                                        "transactionValue": 30.00,
                                                        "senderDocument": "123-456-789-12",
                                                        "receiverDocument": "123-456-789-11",
                                                        "transactionTime": "2025-01-13 12:48:32"
                                                    }
                                                ]
                                            """
                                    }))
                    ),
                    @ApiResponse(responseCode = "401", description = "Resposta ao inserir token como inválido ou vazio", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericError.class, examples = {
                                    """
                                        {
                                            "status": "401",
                                            "message": "Token inválido"
                                        }
                                    """
                            }))),
                    @ApiResponse(responseCode = "400", description = "Resposta ao inserir campos incorretamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericError.class),
                            examples = {
                                    @ExampleObject(name = "Dado faltante", description = "Resposta ao não inserir um dado obrigatório",
                                            value = """
                                            {
                                                "status": "400",
                                                "message": "Insira o dado faltante corretamente: {campo}"
                                            }
                                            """
                                    )
                            })),
                    @ApiResponse(responseCode = "404", description = "Resposta sem registros", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenericError.class), examples = {
                            @ExampleObject(name = "Sem registro", description = "Resposta quando não há registro no banco",
                                    value = """
                                            {
                                                "status": "404",
                                                "message": "Não existem transações"
                                            }
                                            """
                            )
                    })),
                    @ApiResponse(responseCode = "403", description = "Resposta ao usuário não ter permissão para acessar o conteúdo",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericSuccessOutput.class,
                                    example =
                                            """
                                            {
                                                "status": "403",
                                                "message": "O ação que você tentou realizar é proibida"
                                            }
                                            """
                            )))

            })
    @GetMapping("/alltransactions")
    public ResponseEntity<?> getAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @Operation(summary = "Trocar role do usuário", description = "Muda o role do usuário, como 'EMPLOYEE' para 'CLIENT'",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content =
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChangeUserRoleDTO.class),
            examples = {
                    @ExampleObject(name = "To Client", description = "Muda o role do usuário para client",
                            value = """
                                    {
                                        "NewRole" : "CLIENT",
                                        "UserId" : 1
                                    }
                                    """
                    ),
                    @ExampleObject(name = "To ADMIN", description = "Muda o role do usuário para admin",
                            value = """
                                    {
                                        "NewRole" : "ADMIN",
                                        "UserId" : 1
                                    }
                                    """
                    ),
                    @ExampleObject(name = "To Employee", description = "Muda o role do usuário para employee",
                            value = """
                                    {
                                        "NewRole" : "EMPLOYEE",
                                        "UserId" : 1
                                    }
                                    """
                    )
            })))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Resposta ao usuário após realizar a ação com sucesso", content =
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericSuccessOutput.class, example =
                            """
                            {
                                "timestamp": "2025-01-14T19:51:25.5983862",
                                "status": 200,
                                "message": "Role do usuário 123-456-789-10 atualizado com sucesso",
                                "update": "Role: ADMIN -> CLIENT"
                            }
                            """
                    ))),
                    @ApiResponse(responseCode = "401", description = "Resposta ao inserir token como inválido ou vazio", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericError.class, example =
                                    """
                                        {
                                            "status": "401",
                                            "message": "Token inválido"
                                        }
                                    """
                            ))),

                    @ApiResponse(responseCode = "403", description = "Resposta ao usuário não ter permissão para acessar o conteúdo",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericSuccessOutput.class,
                                    example =
                                            """
                                            {
                                                "status": "403",
                                                "message": "O ação que você tentou realizar é proibida"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Respostas ao usuário realizar algum erro",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericError.class), examples = {
                            @ExampleObject(description = "Resposta ao não inserir campo obrigatório",
                                    value = """
                                    {
                                        "status": "400",
                                        "message": "Insira o dado faltante corretamente: {campo}"
                                    }
                                    """,
                                    name = "400 para campos gerais"
                            )
                    })),
                    @ApiResponse(responseCode = "404", description = "Resposta ao não encontrar o usuário", content =
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericError.class, example =
                            """
                            {
                                "status": "404",
                                "message": "Usuário não encontrado."
                            }
                            """
                    )))
            }
    )
    @SecurityRequirement(name = "jwt_auth")
    @PatchMapping("/role")
    public ResponseEntity<?> changeUserRole(@RequestBody ChangeUserRoleDTO data){
        return ResponseEntity.status(HttpStatus.OK).body(userService.changeUserRole(data));
    }
}
