package com.SistemaPagamento.DTOs.Input;

import java.math.BigDecimal;

public record TransactionDTO(
        BigDecimal value,
        Long sender,
        Long receiver
) {
}
