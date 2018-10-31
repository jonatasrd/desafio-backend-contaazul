package com.contaazul.bankslip.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contaazul.bankslip.dto.Payment;
import com.contaazul.bankslip.exception.ResourceNotFoundException;
import com.contaazul.bankslip.model.Bankslip;
import com.contaazul.bankslip.repository.BankslipRepository;

@Service
public class BankslipServiceImpl implements BankslipService {

	@Autowired
	BankslipRepository repository;

	@Autowired
	private FineCalculator fineCalculator;

	@Override
	public Bankslip save(Bankslip bankslip) {
		return repository.save(bankslip);
	}

	@Override
	public Bankslip find(UUID id) {
		Bankslip bankslip = findById(id);
		BigDecimal fine = fineCalculator.calculate(bankslip.getTotalInCents(),
				bankslip.getDueDate());
		if (fine.intValue() > 0)
			bankslip.setFine(fine);
		return bankslip;
	}

	@Override
	public List<Bankslip> findAll() {
		return repository.findAll();
	}

	@Override
	public void pay(UUID id, Payment payment) {
		Bankslip bankslip = findById(id);
		bankslip.pay(payment);
		repository.saveAndFlush(bankslip);
	}

	@Override
	public void cancel(UUID id) {
		Bankslip bankslip = findById(id);
		bankslip.cancel();
		repository.saveAndFlush(bankslip);
	}

	private Bankslip findById(UUID id) {
		return repository.findById(id)
				.orElseThrow(ResourceNotFoundException::new);
	}

}
