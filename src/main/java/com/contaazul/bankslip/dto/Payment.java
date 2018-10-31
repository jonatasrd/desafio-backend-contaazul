package com.contaazul.bankslip.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
	
	@NotNull
	@JsonProperty("payment_date")
	@JsonFormat(pattern="yyyy-MM-dd")
	private LocalDate paymentDate;

}
