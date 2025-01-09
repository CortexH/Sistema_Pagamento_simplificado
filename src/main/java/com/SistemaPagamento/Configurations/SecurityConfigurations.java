package com.SistemaPagamento.Configurations;

import com.SistemaPagamento.Security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.SistemaPagamento.Configurations.EndpointsInfo.EndpointsAuthNotRequired;
import static com.SistemaPagamento.Configurations.EndpointsInfo.SwaggerWhitelist;

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
                        .anyRequest().denyAll() // negar todos os requests sem autenticação, além dos anteriores.
                )
                .cors(cors -> cors.disable()) // desabilitar cors (por enquanto)
                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class) // adicionar o filtro antes
                .build();
    }
}
