package com.SistemaPagamento.DTOs.Output;

public record GenericError(
        String status,
        String message
) {
}
