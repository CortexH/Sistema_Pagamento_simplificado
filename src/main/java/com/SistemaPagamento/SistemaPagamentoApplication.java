package com.SistemaPagamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaPagamentoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SistemaPagamentoApplication.class, args);
	}
}
