package com.contaazul.bankslip.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="bankslip")
@JsonInclude(Include.NON_NULL)
public class Bankslip {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@JsonProperty("due_date")
	@JsonFormat(pattern="yyyy-MM-dd")
	@Column(name="due_date")
	private LocalDate dueDate;
	
	@JsonProperty("total_in_cents")
	@Column(name="total_in_cents")
	private BigDecimal totalInCents;
	
	@Column(name="custumer")
	private String customer;
	
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;
	
	@Column(name="fine")
	private BigDecimal fine;
	
	
	
}
