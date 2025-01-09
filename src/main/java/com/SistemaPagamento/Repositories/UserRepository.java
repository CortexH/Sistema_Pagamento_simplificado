package com.SistemaPagamento.Repositories;

import com.SistemaPagamento.Domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByemail(String email);
    Optional<User> findBydocument(String cpf);

    Boolean existsBydocumentAndPassword(String document, String password);
}
