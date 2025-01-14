package com.SistemaPagamento.Configurations;

import com.SistemaPagamento.Domain.User.UserRoles;
import com.SistemaPagamento.Security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                        .requestMatchers(OnlyForEmployee).hasAnyRole("EMPLOYEE", "ADMIN")
                        .requestMatchers(ProtectedAdminEndpoints).hasRole("ADMIN")
                )
                .cors(cors -> cors.disable()) // desabilitar cors (nota: talvez habilitar mais tarde)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class) // adicionar o filtro antes
                .build();
    }

}
