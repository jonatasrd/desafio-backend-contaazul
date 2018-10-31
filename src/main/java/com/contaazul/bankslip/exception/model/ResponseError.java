package com.contaazul.bankslip.exception.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseError {
    private int status;
    private String error;
    private String msg;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime timestamp;
}
