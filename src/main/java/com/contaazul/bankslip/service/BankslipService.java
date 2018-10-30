package com.contaazul.bankslip.service;

import java.util.List;
import java.util.UUID;

import com.contaazul.bankslip.model.Bankslip;

public interface BankslipService {
	
	Bankslip save(Bankslip bankslip);

	Bankslip findById(UUID id);

	List<Bankslip> findAll();
	
	void pay(UUID id);
	
	void cancel(UUID id);

}
