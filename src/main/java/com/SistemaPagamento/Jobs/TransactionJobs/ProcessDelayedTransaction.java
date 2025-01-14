package com.SistemaPagamento.Jobs.TransactionJobs;

import com.SistemaPagamento.DTOs.Input.ChangeUserBalanceDTO;
import com.SistemaPagamento.DTOs.Input.UserUpdate;
import com.SistemaPagamento.Domain.Transaction.Transaction;
import com.SistemaPagamento.Domain.Transaction.TransactionState;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Services.TransactionService;
import com.SistemaPagamento.Services.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
/*
    Classe para iniciar um processo de atualização das transações
    marcadas como "agendadas" (delayed) com horário de agendamento
    como menor ou igual à data e horário atual;

    e.g : transação agendada para 12:25:00 do dia 2024-01-13, quando o serviço
    rodar nesse horário, essa transação será efetuada.
 */

public class ProcessDelayedTransaction {

    // serviços utilizados
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;


    // inicia o processo de atualização das transações
    public void beginProcess(){
        try{
            // retorna todas as transações e loga o resultado
            ArrayList<Transaction> processedTransactions = new ArrayList<>();
            log.info("PROCESSO DE TRANSAÇÃO INICIADO");
            ArrayList<Transaction> transactions = transactionService.getDelayedTransactions();

            transactions.stream()
                    .filter(transaction -> transaction.getState() == TransactionState.delayed)
                    .forEach(item ->
                            // metodo utilizado para validar e atualizar as transações marcadas como 'delayed'
                    {
                        if(!item.getTransactionTime().isAfter(LocalDateTime.now())){
                            item.setState(TransactionState.completed);

                            User receiver = userService.returnByDocument(item.getReceiverDocument());

                            UserUpdate update = new UserUpdate(receiver.getId(),
                                    receiver.getBalance().add(item.getTransactionValue()), null, null, null, null, null,
                                    null, null, null, null);

                            userService.updateUser(update);
                            processedTransactions.add(item);
                        }
                    });
            // após o serviço realizar a verificação e atualização, atualiza no banco das transações
            pushOnDb(processedTransactions);
            // se nenhum erro ocorrer, finaliza o processo.
            finishProcess();
        } catch (NoSuchElementException e) {
            // se não encontrar transação, loga a informação
            log.info("NENHUMA TRANSAÇÃO AGENDADA DETECTADA");
        } catch (Exception e) {
            // caso ocorra algum erro, loga o erro
            log.error("ERRO INESPERADO : {}", e.getMessage());
        }finally {
            // após tudo ser finalizado, loga o horário de execução
            log.info("HORÁRIO DE EXECUÇÃO :: {} {}", LocalDate.now(), LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }

    public void pushOnDb(ArrayList<Transaction> processedTransactions){
        try{
            // persiste os dados
            transactionService.updateAllTransactions(processedTransactions);
        } catch (RuntimeException e) {
            // caso ocorra algum erro ao persistir os dados, loga uma mensagem de erro
            log.error("FALHA AO PERSISTIR DADOS :: {}", e.getMessage(), e);
            return;
        }
        // loga a quantidade de transações processadas
        log.info("QUANTIDADE DE TRANSAÇÕES PROCESSADAS :: {}", processedTransactions.size());
    }

    public void finishProcess(){
        // loga informando que o processo foi finalizado
        log.info("PROCESSO FINALIZADO");
    }
}
