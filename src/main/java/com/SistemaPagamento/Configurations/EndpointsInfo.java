package com.SistemaPagamento.Configurations;

public class EndpointsInfo {

    public static final String[] EndpointsAuthNotRequired = {
            "/user/login",
            "/user/new",
    };

    public static final String[] SwaggerWhitelist = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui"
    };

}
