package com.SistemaPagamento.DTOs.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticatedUser{
    String status;
    String token;
}
