package com.contaazul.bankslip.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Bankslip {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	private LocalDate dueDate;
	private BigDecimal totalInCents;
	private String customer;
	
	@Enumerated(EnumType.STRING)
	private Status status;
}
