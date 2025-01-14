package com.SistemaPagamento.Configurations;

import com.SistemaPagamento.DTOs.Output.GenericError;
import com.SistemaPagamento.Domain.User.UserRoles;
import com.SistemaPagamento.Security.AuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

import static com.SistemaPagamento.Configurations.EndpointsInfo.*;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable()) // Desabilitar CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointsAuthNotRequired).permitAll() // permitir requests sem autenticação
                        .requestMatchers(SwaggerWhitelist).permitAll() // permite requests do swagger
                        .requestMatchers(ProtectedClientEndpoints).hasAnyRole("CLIENT", "ADMIN", "EMPLOYEE") // permite estes requests para user com role "Client"
                        .requestMatchers(OnlyForEmployee).hasAnyRole("EMPLOYEE", "ADMIN") // permite estes requests para user com role 'employee' e 'admin'
                        .requestMatchers(ProtectedAdminEndpoints).hasRole("ADMIN") // permite estes requests apenas para user com role 'admin'
                )
                .exceptionHandling(exp -> exp.accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(403);

                    GenericError err = new GenericError("403", "O ação que você tentou realizar é proibida");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(err));
                    response.getWriter().flush();

                }))
                .cors(cors -> cors.disable()) // desabilitar cors (nota: talvez habilitar mais tarde)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class) // adicionar o filtro antes
                .build();
    }

}
