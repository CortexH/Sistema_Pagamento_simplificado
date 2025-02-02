package com.SistemaPagamento.Controller;

import com.SistemaPagamento.DTOs.Input.UserDTO;
import com.SistemaPagamento.DTOs.Input.UserLoginDTO;
import com.SistemaPagamento.DTOs.Output.AuthenticatedUser;
import com.SistemaPagamento.DTOs.Output.GenericError;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Services.JwtService;
import com.SistemaPagamento.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
@Tag(name = "Public Endpoints", description = "Endpoints públicos relacionados ao usuário.")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @Operation(
            summary = "Cria um novo usuário.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto representante dos dados do usuário",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    name = "Usuário",
                                    value = """
                                            {
                                                "firstName" : "Ronaldinho",
                                                "lastName" : "Gaúcho",
                                                "password" : "RonaldinhoGaucho123",
                                                "email" : "ronaldoGauchinho@example.com",
                                                "document" : "123-456-789-10",
                                                "classification" : "Common"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cria um novo usuário", content =
            @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticatedUser.class,
                            example =  """
                                        {
                                            "status" : "200",
                                            "token" : "abc..."
                                        }
                                        """
                    )

            )),
            @ApiResponse(responseCode = "400", description = "Erros relacionados ao Bad Request, 400",
                    content = {
                    @Content(schema = @Schema(implementation = GenericError.class), examples = {
                            @ExampleObject(
                                    name = "Erro de campo mal preenchido",
                                    value = """
                                            {
                                                "status" : "400",
                                                "message" : "Usuário com document especificado já existe"
                                            }
                                            """
                            )
                    }
                    )})

    })
    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody UserDTO data) throws Exception {
        try{
            String token = userService.newUser(data);
            AuthenticatedUser auth = new AuthenticatedUser("200", token);
            return ResponseEntity.status(HttpStatus.OK).body(auth);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Operation(summary = "User login", description = "Login do usuário", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = {
            @Content(schema = @Schema(implementation = UserLoginDTO.class, example =
                    """
                    {
                        "document" : "123-456-789-10",
                        "password" : "12345"
                    }
                    """
            ))
    }),
            responses = {
                @ApiResponse(responseCode = "200", description = "Retorna o código de resposta 200 junto ao token", content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = GenericError.class, example =
                                """
                                {
                                    "status" : "200",
                                    "token" : "abc..."
                                }
                                """
                        )
                )),
                @ApiResponse(responseCode = "400", description = "Respostas Bad Request", content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericError.class),
                        examples = {
                                @ExampleObject(name = "Dado faltante", description = "Resposta ao não inserir um dado obrigatório",
                                    value = """
                                            {
                                                "status": "400",
                                                "message": "Insira o dado faltante corretamente: {campo}"
                                            }
                                            """
                                ),
                                @ExampleObject(name = "Dados incorretos", description = "Resposta ao inserir algum dado incorretamente",
                                        value = """
                                                {
                                                    "status": "400",
                                                    "message": "CPF ou senha incorretos."
                                                }
                                                """
                                )
                        }
                ))
    }
    )
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO data){
        String token = userService.userLogin(data);
        AuthenticatedUser auth = new AuthenticatedUser("200", token);
        return ResponseEntity.status(HttpStatus.OK).body(auth);
    }

}
