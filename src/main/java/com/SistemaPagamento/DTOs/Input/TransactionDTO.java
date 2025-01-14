package com.SistemaPagamento.DTOs.Input;

import com.SistemaPagamento.Domain.Transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
        BigDecimal value,
        String receiver,
        TransactionType type,
        LocalDateTime transactionTime
) {
}
