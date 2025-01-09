package com.SistemaPagamento.Domain.Transaction;

import com.SistemaPagamento.Domain.User.User;
import jakarta.persistence.*;

@Entity
@Table(name = "Transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "User_Sender")
    private User sender;

    @ManyToOne
    @JoinColumn(nullable = false, name = "User_Receiver")
    private User receiver;

    private TransactionType type;
    private TransactionState state;

}
