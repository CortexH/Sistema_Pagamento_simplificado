package com.SistemaPagamento.Security;

import com.SistemaPagamento.Configurations.EndpointsInfo;
import com.SistemaPagamento.Repositories.UserRepository;
import com.SistemaPagamento.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Arrays;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
        Swagger é "público" pela parte da api. Por isso vamos desabilitar a autenticação para o swagger.
        Caso ocorra algum problema, remover.
         */

        if(request.getRequestURI().startsWith("/v3/api-docs") || request.getRequestURI().startsWith("/swagger-ui")){
            filterChain.doFilter(request, response);
            return;
        }

        if(!checkAllowedEndpoint(request)){
            try{
                String token = getToken(request);
                String subject = jwtService.getTokenSubject(token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(subject, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header != null){
            return header.substring(7);
        }
        throw new RuntimeException("Usuário não autenticado.");
    }

    private boolean checkAllowedEndpoint(HttpServletRequest request){
        String reqURI = request.getRequestURI();
        return Arrays.asList(EndpointsInfo.EndpointsAuthNotRequired).contains(reqURI);
    }

}
