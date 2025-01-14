package com.SistemaPagamento.DTOs.Input;

import com.SistemaPagamento.Domain.User.UserClassification;
import com.SistemaPagamento.Domain.User.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserUpdate(
        Long userId,
        BigDecimal balance,
        String firstName,
        String lastName,
        String password,
        String email,
        String document,
        UserClassification classification,
        UserRoles role,
        Boolean blocked,
        Boolean deleted

) {
}
