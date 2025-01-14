package com.SistemaPagamento.DTOs.Input;

import com.SistemaPagamento.Domain.User.UserSetBalance;

import java.math.BigDecimal;

public record ChangeUserBalanceDTO(
        UserSetBalance balanceOperation,
        BigDecimal inputValue,
        String reason,
        Long adminId,
        Long userId
) {
}
