package com.SistemaPagamento.Repositories;

import com.SistemaPagamento.Domain.Transaction.Transaction;
import com.SistemaPagamento.Domain.Transaction.TransactionType;
import com.SistemaPagamento.Domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<ArrayList<Transaction>> findByType(TransactionType type);
    Optional<ArrayList<Transaction>> findAllBySender(User user);

}
