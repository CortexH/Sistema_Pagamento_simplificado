package com.SistemaPagamento.Domain.BalanceUpdate;

import com.SistemaPagamento.Domain.User.User;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Balance_Update")
@Setter
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String message;

    @ManyToOne
    @JoinColumn(name = "Admin_Id")
    private User admin;

    @ManyToOne
    @JoinColumn(name = "Changed_User_Id", nullable = false)
    private User changedUser;
}
