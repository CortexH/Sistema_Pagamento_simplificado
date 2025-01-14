package com.SistemaPagamento.Services;

import com.SistemaPagamento.DTOs.Input.CancelDelayedTransactionDTO;
import com.SistemaPagamento.DTOs.Input.TransactionDTO;
import com.SistemaPagamento.DTOs.Output.GenericError;
import com.SistemaPagamento.DTOs.Output.GenericSuccessOutput;
import com.SistemaPagamento.Domain.Transaction.Transaction;
import com.SistemaPagamento.Domain.Transaction.TransactionState;
import com.SistemaPagamento.Domain.Transaction.TransactionType;
import com.SistemaPagamento.Domain.User.User;
import com.SistemaPagamento.Repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Slf4j
public class TransactionService {

    // encontrando o repositório JPA das transações
    @Autowired
    private TransactionRepository transactionRepository;

    // serviços utilizados
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceService userBalanceService;

    // metodo para criar uma nova transação
    public GenericSuccessOutput newTransaction(TransactionDTO data, String token) throws Exception {

        // puxar o sender (usuário que enviou) com base no token de verificação
        User sender = userService.returnByDocument(jwtService.getTokenSubject(token));

        // puxar o receiver (usuário que recebeu) com base no Document inserido no TransactionDTO
        User receiver = userService.returnByDocument(data.receiver());

        validadeUserBalance(sender, receiver, data.value());

        // validar se o valor da transação é maior que 0
        if(data.value().intValue() <= 0)
            throw new IllegalArgumentException("O valor da transação deverá ser maior que 0");

        // criar um novo registro de transação
        Transaction transaction = new Transaction();

        // adicionar as propriedades à entidade
        transaction.setTransactionValue(data.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setType(data.type());
        transaction.setReceiverDocument(receiver.getDocument());
        transaction.setSenderDocument(sender.getDocument());

        // controle de fluxo caso a transação for agendada
        if(transaction.getType() == TransactionType.delayed){

            LocalDateTime transactionTime = data.transactionTime();
            LocalDateTime localDateTime = LocalDateTime.now();

            // validação do horário colocado na propriedade transactionTime
            if(localDateTime.plusMonths(1).isBefore(transactionTime)){
                throw new IllegalArgumentException("Data maior do que o tempo permitido.");
            } else if(LocalDateTime.now().isAfter(transactionTime)) {
                throw new IllegalArgumentException("Data inserida menor que a data atual.");
            }

            // o intervalo entre cada ponto do transaction time é de 5 (o usuário só pode colocar os minutos como múltiplo de 5)
            if(transactionTime.getMinute() % 5 != 0){
                throw new IllegalArgumentException("A horário deve seguir um padrão de 5 em 5 minutos!");
            }

            // atualizar mais propriedades
            transaction.setTransactionTime(transactionTime);
            transaction.setState(TransactionState.delayed);
        }else{
            transaction.setTransactionTime(LocalDateTime.now());
        }
        sender.setBalance(sender.getBalance().subtract(data.value()));

        transactionRepository.save(transaction);

        GenericSuccessOutput suc = new GenericSuccessOutput();
        suc.setTimestamp(LocalDateTime.now());
        suc.setStatus(200);
        suc.setMessage("Transação realizada com sucesso!");
        suc.setUpdate("new Transaction added");


        return suc;
    }

    // metodo para validar se userBalance > 0
    public void validadeUserBalance(User sender, User receiver, BigDecimal balance) throws BadRequestException {
        if(sender.getBalance().compareTo(balance) < 0){
            throw new IllegalArgumentException("Sender não tem saldo suficiente.");
        }
    }

    // metodo que encontra todas as transações marcadas como delayed
    public ArrayList<Transaction> getDelayedTransactions(){
        ArrayList<Transaction> allDelayedTransactions = transactionRepository
                .findByType(TransactionType.delayed)
                .orElseThrow(() -> new NoSuchElementException("Nenhuma transação delayed encontrada"));

        if(allDelayedTransactions.isEmpty()) throw new NoSuchElementException();

        return allDelayedTransactions;
    }

    // Atualiza todas as transações dentro do arraylist
    public void updateAllTransactions(ArrayList<Transaction> data){
        try{
            transactionRepository.saveAll(data);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // metodo para retornar todas as transações
    public List<Transaction> getAllTransactions(){
        List<Transaction> allTransactions = transactionRepository.findAll();
        if(allTransactions.isEmpty()) throw new NoSuchElementException("Não existem transações");

        return allTransactions;
    }

    // cancela uma transação específica, caso ela for marcada como "delayed"
    public GenericSuccessOutput cancelTransaction(String token, CancelDelayedTransactionDTO data){

        // lança exceção se transactionId for nulo
        Objects.requireNonNull(data.transactionId(), "transactionId");

        // puxa o document do usuário conforme o token
        String cpf = jwtService.getTokenSubject(token);

        // puxa o usuário com base no cpf (document)
        User sender = userService.returnByDocument(cpf);

        // puxa todas as transações com base em quem mandou elas
        ArrayList<Transaction> transactions = transactionRepository.findAllBySender(sender)
                .orElseThrow(() -> new NoSuchElementException("Não foram encontradas nenhuma transação com o usuário " + sender.getFirstName() + " " + sender.getLastName()));

        // validação da existência de pelo menos uma transação
        if(transactions == null || transactions.isEmpty()) throw new NoSuchElementException("Não foram encontradas nenhuma transação com o usuário " + sender.getFirstName() + " " + sender.getLastName());

        // puxar apenas a transação com o ID especificado em 'data'
        List<Transaction> listTransaction = transactions.stream().filter(item -> item.getId().equals(data.transactionId())).toList();

        // validação da existência de uma transação com o id especificado
        if(listTransaction.isEmpty()) throw new NoSuchElementException("Usuário não tem nenhuma transação com o ID especificado.");

        Transaction transaction = listTransaction.getFirst();

        // validação do tipo de transação
        if(transaction.getType() != TransactionType.delayed){
            throw new IllegalArgumentException("Tipo da transação é imediata e não pode ser cancelada! contate o suporte caso queira cancelar a transação.");
        }

        // atualizando o estado da transação como cancelado
        transaction.setState(TransactionState.canceled);

        // atualizando o saldo do usuário
        userBalanceService.onlyChangeBalance(sender, sender.getBalance().add(transaction.getTransactionValue()));

        // criando objeto com mensagem de sucesso e atualização
        transactionRepository.save(transaction);
        GenericSuccessOutput suc = new GenericSuccessOutput();
        suc.setStatus(200);
        suc.setMessage("Transação cancelada com sucesso");
        suc.setTimestamp(LocalDateTime.now());
        suc.setUpdate("Transaction: Delayed -> Canceled");

        return suc;
    }

}
