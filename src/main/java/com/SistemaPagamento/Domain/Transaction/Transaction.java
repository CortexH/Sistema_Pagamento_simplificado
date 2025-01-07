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
    @JoinColumn(nullable = false, table = "Sender_Id", name = "User_Sender")
    private User sender;

    @ManyToOne
    @JoinColumn(nullable = false, table = "Receiver_Id", name = "User_Receiver")
    private User receiver;

    private TransactionType type;
    private TransactionState state;

}
