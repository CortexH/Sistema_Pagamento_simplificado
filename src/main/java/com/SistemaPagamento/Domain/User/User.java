package com.SistemaPagamento.Domain.User;

import com.SistemaPagamento.DTOs.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private UserClassification classification;

    private Boolean blocked;
    private Boolean deleted;

    private BigDecimal balance;

    public User(UserDTO data){

        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.password = data.password();
        this.email = data.email();
        this.classification = data.classification();

        this.blocked = false;
        this.deleted = false;

        this.balance = new BigDecimal(0);
    }

}
