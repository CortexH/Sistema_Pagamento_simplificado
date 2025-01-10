package com.SistemaPagamento.Services;

import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findBydocument(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new com.SistemaPagamento.Domain.User.UserDetails(user);
    }
}
