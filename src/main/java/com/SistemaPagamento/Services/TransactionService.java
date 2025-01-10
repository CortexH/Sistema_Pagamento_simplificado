package com.SistemaPagamento.Services;

import com.SistemaPagamento.DTOs.Input.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class TransactionService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

}
