package com.SistemaPagamento.Domain.User;

import com.SistemaPagamento.DTOs.Input.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(name = "Password")
    private String password;

    private String email;
    private String document;
    private UserClassification classification;

    private Boolean blocked;
    private Boolean deleted;

    private BigDecimal balance;

    private UserRoles role;

    public User() {
    }

    public User(UserDTO data){
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.password = data.password();
        this.email = data.email();
        this.document = data.document();
        this.classification = data.classification();
        this.role = UserRoles.ADMIN;

        this.blocked = false;
        this.deleted = false;

        this.balance = new BigDecimal(0);
    }



}
