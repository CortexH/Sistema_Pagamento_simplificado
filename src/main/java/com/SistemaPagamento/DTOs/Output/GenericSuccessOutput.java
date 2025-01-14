package com.SistemaPagamento.DTOs.Output;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GenericSuccessOutput {

    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private String update;

}
