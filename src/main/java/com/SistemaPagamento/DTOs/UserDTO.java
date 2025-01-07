package com.SistemaPagamento.DTOs;

import com.SistemaPagamento.Domain.User.UserClassification;

// DTO para criação de um usuário
public record UserDTO(
        String firstName,
        String lastName,
        String password,
        String email,
        String document, // CPF no brasil
        UserClassification classification
) { }
