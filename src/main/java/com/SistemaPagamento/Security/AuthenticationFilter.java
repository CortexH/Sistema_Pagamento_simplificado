package com.SistemaPagamento.Security;

import com.SistemaPagamento.Configurations.EndpointsInfo;
import com.SistemaPagamento.DTOs.Output.GenericError;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Domain.User.UserDetails;
import com.SistemaPagamento.Services.JwtService;
import com.SistemaPagamento.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, AuthorizationDeniedException {
        try{
             /*
            Swagger é "público" pela parte da api. Por isso vamos desabilitar a autenticação para o swagger.
            nota: Caso ocorra algum problema, remover.
             */
            if(request.getRequestURI().startsWith("/v3/api-docs") || request.getRequestURI().startsWith("/swagger-ui")){
                filterChain.doFilter(request, response);
                return;
            }

            // verifica se o endpoint atual não é permitido sem autenticação
            if(!checkAllowedEndpoint(request)){

                // recupera o token e o subject da requisição
                String token = jwtService.getTokenFromHttp(request);
                String subject = jwtService.getTokenSubject(token);

                // se o token for nulo, lança uma exceção
                if(subject == null){
                    throw new AuthorizationDeniedException("Usuário não autorizado");
                }

                // retorna o usuário com base no token
                User user = userService.returnByDocument(subject);
                UserDetails userDetails = new UserDetails(user);

                // realiza a autenticação do usuário
                Authentication authentication = new UsernamePasswordAuthenticationToken(jwtService.getTokenSubject(token), null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // realiza o filtro com sucesso.
                filterChain.doFilter(request, response);
                return;

            }
            // realiza o filtro 'cru' se o endpoint não precisar de autenticação
            filterChain.doFilter(request, response);

        // captura de exceções com suas respostas personalizadas.
        } catch (AuthorizationDeniedException e) {

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            GenericError err = new GenericError(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getMessage());
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(err));
            response.getWriter().flush();

        } catch (NoSuchElementException e) {

            response.setStatus(HttpStatus.NOT_FOUND.value());
            GenericError err = new GenericError(String.valueOf(HttpStatus.NOT_FOUND.value()), e.getMessage());
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(err));
            response.getWriter().flush();

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            GenericError err = new GenericError(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token inválido");
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(err));
            response.getWriter().flush();

        }
    }

    // metodo para validar se o endpoint da requisição é permitido sem autenticação
    private boolean checkAllowedEndpoint(HttpServletRequest request){
        String reqURI = request.getRequestURI();
        return Arrays.asList(EndpointsInfo.EndpointsAuthNotRequired).contains(reqURI);
    }

}
