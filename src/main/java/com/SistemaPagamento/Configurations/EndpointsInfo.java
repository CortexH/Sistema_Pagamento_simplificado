package com.SistemaPagamento.Configurations;

public class EndpointsInfo {

    // endpoints que não precisam de autenticação
    public static final String[] EndpointsAuthNotRequired = {
            "/user/login",
            "/user/new",
    };

    // endpoints acessados pelo swagger
    public static final String[] SwaggerWhitelist = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui"
    };

    // endpoints protegidos, acessados apenas por usuários autenticados
    public static final String[] ProtectedClientEndpoints = {
            "/transaction/new",
            "/transaction/**"
    };

    // endpoints protegidos, acessados apenas por administradores
    public static final String[] ProtectedAdminEndpoints = {
            "/admin/**",
            "/admin/getUsers",
            "/admin/SetUserBalance",
            "/admin/alltransactions"
    };

    // endpoints protegidos, acessados apenas por colaboradores
    public static final String[] OnlyForEmployee = {
        "/services/**"
    };


}
