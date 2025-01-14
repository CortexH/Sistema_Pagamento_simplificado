package com.SistemaPagamento.Domain.Transaction;

public enum TransactionState {
    delayed, // foi enviada, mas está em aguardo (TransactionType = delayed)
    completed, // foi concluída com sucesso
    canceled // foi cancelada por algum motivo

}
