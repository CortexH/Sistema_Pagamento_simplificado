package com.SistemaPagamento.DTOs.Output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
public class AllMessagesSuccessOutput {

    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private ArrayList<String> messages;

}
