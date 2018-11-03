package com.contaazul.bankslip.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.contaazul.bankslip.dto.Payment;
import com.contaazul.bankslip.exception.InvalidStatusException;

public class BankslipTest {
	
	private LocalDate dueDate;
	private Bankslip bankslip;
	private Payment payment;

	@Before
	public void setUp() {
		dueDate = LocalDate.of(2018, 10, 1);
		payment = Payment.builder().paymentDate(LocalDate.of(2018, 10, 15)).build();
		bankslip = Bankslip.builder().customer("Yahoo").status(Status.PENDING).dueDate(dueDate)
				.totalInCents(new BigDecimal(1000)).build();
	}
	
	@Test
	public void whenCancelBankslip_shouldReturnBankslipCanceled() {
		Bankslip bankslipCanceled = bankslip.cancel();
		assertThat(bankslipCanceled.getStatus()).isEqualTo(Status.CANCELED);
	}
	
	@Test
	public void whenPayBankslip_shouldThrowBankslipPaid() {
		Bankslip bankslipCanceled = bankslip.pay(payment);
		assertThat(bankslipCanceled.getStatus()).isEqualTo(Status.PAID);
		assertThat(bankslipCanceled.getPaymentDate()).isEqualTo(payment.getPaymentDate());
	}
	
	@Test(expected=InvalidStatusException.class)
	public void whenCancelACanceledBankslip_shouldThrowAlreadyCanceled() {
		Bankslip canceledBankslip = bankslip.cancel();
		canceledBankslip.cancel();
	}
	
	@Test(expected=InvalidStatusException.class)
	public void whenPayACanceledBankslip_shouldThrowAlreadyCanceled() {
		Bankslip canceledBankslip = bankslip.cancel();
		canceledBankslip.pay(payment);
	}

	@Test(expected=InvalidStatusException.class)
	public void whenCancelAPaidBankslip_shouldThrowAlreadyPaid() {
		Bankslip paidBankslip = bankslip.pay(payment);
		paidBankslip.cancel();
	}
	
	@Test(expected=InvalidStatusException.class)
	public void whenPayAPaidBankslip_shouldThrowAlreadyPaid() {
		Bankslip paidBankslip = bankslip.pay(payment);
		paidBankslip.pay(payment);
	}
}
