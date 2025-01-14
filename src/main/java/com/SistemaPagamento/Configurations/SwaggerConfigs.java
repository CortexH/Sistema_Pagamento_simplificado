package com.SistemaPagamento.Configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// configurações do swagger

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Sistema Pagamento", // titulo
        description = "Sistema de pagamento simplificado para Portfolio" // descrição
), security = @SecurityRequirement(name = "jwt_auth")) // metodo de segurança
@SecurityScheme(
        name = "jwt_auth", // nome do scheme
        scheme = "bearer", // esquema 'bearer'
        type = SecuritySchemeType.HTTP, // tipo da segurança
        bearerFormat = "JWT" // formato do bearer
)
public class SwaggerConfigs {

}
