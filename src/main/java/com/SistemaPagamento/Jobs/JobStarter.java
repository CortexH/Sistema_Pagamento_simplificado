package com.SistemaPagamento.Jobs;

import com.SistemaPagamento.Jobs.TransactionJobs.ProcessDelayedTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobStarter {

    @Autowired
    private ProcessDelayedTransaction processDelayedTransaction;

    // schedule executado a cada 5 minutos que inicia o metodo beginProcess de processDelayedTransaction
    @Scheduled(scheduler = "UpdateDelayedTransaction", cron = "0 */5 * * * *")
    public void updateDelayedTransactions(){ processDelayedTransaction.beginProcess(); }

}
