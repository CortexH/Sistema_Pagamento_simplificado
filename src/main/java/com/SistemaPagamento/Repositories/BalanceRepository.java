package com.SistemaPagamento.Repositories;

import com.SistemaPagamento.Domain.BalanceUpdate.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
