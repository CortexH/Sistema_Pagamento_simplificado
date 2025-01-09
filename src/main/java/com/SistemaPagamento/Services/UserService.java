package com.SistemaPagamento.Services;

import com.SistemaPagamento.DTOs.Input.UserDTO;
import com.SistemaPagamento.DTOs.Input.UserLoginDTO;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    //metodo para retornar usuário pelo email, se não encontrar, lança uma exceção
    public User returnByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByemail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // metodo para retornar usuário pelo document, se não encontrar, lança uma exceção
    public User returnByDocument(String doc) throws Exception{
        return userRepository.findBydocument(doc)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // metodo para retornar todos os usuários
    public List<User> returnAllUsers(){
        return userRepository.findAll();
    }

    // metodo para criar um novo usuário e inseri-lo no banco
    public String newUser(UserDTO userDTO){
        try{
            if(!validateAbleToCreateUser(userDTO)) throw new BadRequestException("Usuário com email ou document especificado já existe");
            User user = new User(userDTO);
            User userSaved = userRepository.save(user);
            return jwtService.generateToken(userSaved.getDocument());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // metodo para validar se o usuário pode ser criado
    public Boolean validateAbleToCreateUser(UserDTO data){
        return !(userRepository.findByemail(data.document()).isPresent()
                || userRepository.findBydocument(data.document()).isPresent());
    }

    public String userLogin(UserLoginDTO data){
        if(!userRepository.existsBydocumentAndPassword(data.document(), data.password())) throw new NoSuchElementException("CPF ou senha incorretos.");
        return jwtService.generateToken(data.document());
    }
}
