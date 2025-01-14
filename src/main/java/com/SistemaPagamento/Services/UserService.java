package com.SistemaPagamento.Services;

import com.SistemaPagamento.DTOs.Input.ChangeUserRoleDTO;
import com.SistemaPagamento.DTOs.Input.UserDTO;
import com.SistemaPagamento.DTOs.Input.UserLoginDTO;
import com.SistemaPagamento.DTOs.Input.UserUpdate;
import com.SistemaPagamento.DTOs.Output.GenericSuccessOutput;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Domain.User.UserRoles;
import com.SistemaPagamento.Repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    // metodo para retornar usuário pelo document, se não encontrar, lança uma exceção
    public User returnByDocument(String doc){
        return userRepository.findBydocument(doc)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com document especificado não pôde ser encontrado"));
    }

    // metodo para retornar todos os usuários
    public List<User> returnAllUsers(){
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) throw new NoSuchElementException("Nenhum usuário foi encontrado");

        return users;
    }

    public User returnById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));
    }

    // metodo para criar um novo usuário e inseri-lo no banco
    public String newUser(UserDTO userDTO){

        Objects.requireNonNull(userDTO.document(), "document");
        Objects.requireNonNull(userDTO.classification(), "classification");
        Objects.requireNonNull(userDTO.email(), "email");
        Objects.requireNonNull(userDTO.password(), "password");
        Objects.requireNonNull(userDTO.firstName(), "firstName");
        Objects.requireNonNull(userDTO.lastName(), "lastName");

        try{
            if(!validateAbleToCreateUser(userDTO)) throw new IllegalArgumentException("Usuário com document especificado já existe");
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

    // metodo para logar com o usuário
    public String userLogin(UserLoginDTO data){

        Objects.requireNonNull(data.document(), "document");
        Objects.requireNonNull(data.password(), "password");

        if(!userRepository.existsBydocumentAndPassword(data.document(), data.password())) throw new IllegalArgumentException("CPF ou senha incorretos.");
        return jwtService.generateToken(data.document());
    }

    // metodo para atualizar todos os dados do usuário e retornar um arraylist das atualizações
    public ArrayList<String> updateUser(UserUpdate data) {

        ArrayList<String> updated = new ArrayList<>();

        User user = returnById(data.userId());

        if(data.firstName() != null) user.setFirstName(data.firstName()); updated.add(user.getFirstName() + " -> " + data.firstName());
        if(data.lastName() != null) user.setLastName(data.lastName()); updated.add(user.getLastName() + " -> " + data.lastName());
        if(data.balance() != null) user.setBalance(data.balance()); updated.add(user.getBalance() + " -> " + data.balance());
        if(data.classification() != null) user.setClassification(data.classification()); updated.add(user.getClassification() + " -> " + data.classification());
        if(data.blocked() != null) user.setBlocked(data.blocked()); updated.add(user.getBlocked() + " -> " + data.blocked());
        if(data.deleted() != null) user.setDeleted(data.deleted()); updated.add(user.getDeleted() + " -> " + data.deleted());
        if(data.document() != null) user.setDocument(data.document()); updated.add(user.getDocument() + " -> " + data.document());
        if(data.password() != null) user.setPassword(data.password()); updated.add(user.getPassword() + " -> " + data.password());
        if(data.role() != null) user.setRole(data.role()); updated.add(user.getRole() + " -> " + data.role());
        if(data.email() != null) user.setEmail(data.email()); updated.add(user.getEmail() + " -> " + data.email());

        userRepository.save(user);

        return updated;
    }

    public GenericSuccessOutput changeUserRole(ChangeUserRoleDTO data){
        User user = returnById(data.UserId());
        UserRoles lastRole = user.getRole();

        if(user.getRole().equals(data.NewRole())){
            throw new IllegalArgumentException("Role inalterado.");
        }

        user.setRole(data.NewRole());

        userRepository.save(user);

        GenericSuccessOutput suc = new GenericSuccessOutput();
        suc.setStatus(200);
        suc.setMessage("Role do usuário " + user.getDocument() + " atualizado com sucesso");
        suc.setTimestamp(LocalDateTime.now());
        suc.setUpdate("Role: " + lastRole.name() + " -> " + user.getRole().name());

        return suc;
    }

}
