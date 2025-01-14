package com.SistemaPagamento.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

// configurações scheduler (job)
@Configuration
public class SchedulerConfig {

    @Bean(name = "UpdateDelayedTransaction")
    public ThreadPoolTaskScheduler updateDelayedTransactionTask(){
        ThreadPoolTaskScheduler task = new ThreadPoolTaskScheduler();
        task.setPoolSize(1); // limite de threads
        task.setThreadNamePrefix("SysPag-"); // prefixo do nome
        task.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        task.setRemoveOnCancelPolicy(true);

        return task;
    }


}
