package com.contaazul.bankslip.api.v1;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.contaazul.bankslip.dto.Payment;
import com.contaazul.bankslip.model.Bankslip;
import com.contaazul.bankslip.service.BankslipService;

@RestController
@RequestMapping("/bankslips")
@RequestScope
public class BankslipRestService {

	@Autowired
	private BankslipService service;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Bankslip save(@Valid @RequestBody Bankslip bankslip) {
		return service.save(bankslip);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Iterable<Bankslip> findAll() {
		return service.findAll();
	}

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Bankslip findById(@PathVariable UUID id) {
		return service.find(id);
	}

	@PostMapping("{id}/payments")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void pay(@PathVariable UUID id, @RequestBody Payment payment) {
		service.pay(id, payment);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancel(@PathVariable UUID id) {
		service.cancel(id);
	}

}
