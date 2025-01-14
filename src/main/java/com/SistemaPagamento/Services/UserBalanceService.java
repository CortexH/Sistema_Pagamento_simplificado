package com.SistemaPagamento.Services;

import com.SistemaPagamento.DTOs.Input.ChangeUserBalanceDTO;
import com.SistemaPagamento.DTOs.Input.UserUpdate;
import com.SistemaPagamento.DTOs.Output.GenericSuccessOutput;
import com.SistemaPagamento.Domain.BalanceUpdate.Balance;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Domain.User.UserSetBalance;
import com.SistemaPagamento.Repositories.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserBalanceService {

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceRepository balanceRepository;

    // metodo para mudar o userBalance, adicionando log no banco.
    public GenericSuccessOutput changeUserBalance(ChangeUserBalanceDTO data){

        Objects.requireNonNull(data.inputValue(), "inputValue");
        Objects.requireNonNull(data.userId(), "userId");
        Objects.requireNonNull(data.adminId(), "adminId");
        Objects.requireNonNull(data.balanceOperation(), "balanceOperation");
        Objects.requireNonNull(data.reason(), "reason");

        Balance balance = new Balance();

        User changedUser = userService.returnById(data.userId());
        User admin = userService.returnById(data.adminId());

        BigDecimal lastBalance = changedUser.getBalance();

        balance.setChangedUser(changedUser);
        balance.setAdmin(admin);
        balance.setMessage(data.reason());
        balance.setTimestamp(LocalDateTime.now());

        balanceRepository.save(balance);

        UserUpdate upd = new UserUpdate(data.userId(),
                newBalanceOperation(data, changedUser.getBalance()),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
                );

        userService.updateUser(upd);

        GenericSuccessOutput output = new GenericSuccessOutput();
        output.setTimestamp(LocalDateTime.now());
        output.setStatus(200);
        output.setMessage("Saldo do usuário " + changedUser.getFirstName() + " " + changedUser.getLastName() + " atualizado com sucesso!");
        output.setUpdate("Saldo: " + lastBalance + " -> " + changedUser.getBalance());

        return output;
    }

    // mudar só o balance do usuário, sem inserir nenhum log no banco.
    public Boolean onlyChangeBalance(User user, BigDecimal newBalance){
        user.setBalance(newBalance);

        UserUpdate upd = new UserUpdate(user.getId(),
                newBalance,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        userService.updateUser(upd);
        return true;
    }

    // metodo local para calcular o balance do usuario com base no ChangeUserBalanceDTO.UserSetBalance
    private BigDecimal newBalanceOperation(ChangeUserBalanceDTO data, BigDecimal balance){

        UserSetBalance operation = data.balanceOperation();

        if(operation.equals(UserSetBalance.SET)){
            return data.inputValue();
        } else if(operation.equals(UserSetBalance.MINUS)){
            return balance.subtract(data.inputValue());
        } else if (operation.equals(UserSetBalance.PLUS)){
            return balance.add(data.inputValue());
        }else throw new IllegalArgumentException("Insira uma operação válida");

    }
}
