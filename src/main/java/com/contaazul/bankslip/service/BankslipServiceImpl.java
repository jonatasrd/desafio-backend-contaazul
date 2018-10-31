package com.contaazul.bankslip.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contaazul.bankslip.model.Bankslip;
import com.contaazul.bankslip.repository.BankslipRepository;

@Service
public class BankslipServiceImpl implements BankslipService {
	
	@Autowired
	BankslipRepository repository;

	@Override
	public Bankslip save(Bankslip bankslip) {
		return repository.save(bankslip);
	}

	@Override
	public Bankslip findById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Bankslip> findAll() {
		return repository.findAll();
	}

	@Override
	public void pay(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel(UUID id) {
		// TODO Auto-generated method stub
		
	}

}
