package com.SistemaPagamento.Domain.Transaction;

import com.SistemaPagamento.DTOs.Input.TransactionDTO;
import com.SistemaPagamento.Domain.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "User_Sender")
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(nullable = false, name = "User_Receiver")
    @JsonBackReference
    private User receiver;

    @Column(name = "Type")
    private TransactionType type;
    @Column(name = "State")
    private TransactionState state;
    private BigDecimal transactionValue;
    private String senderDocument;
    private String receiverDocument;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = true, name = "transactionTime")
    private LocalDateTime transactionTime;

}


