package com.contaazul.bankslip.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.contaazul.bankslip.model.Bankslip;

@RunWith(SpringRunner.class)
public class FineCalculatorServiceTest {

	@TestConfiguration
	static class FineCalculatorServiceTestContextConfiguration {
		@Bean
		public FineCalculatorService fineCalculatorService() {
			return new FineCalculatorService();
		}
	}

	@Autowired
	private FineCalculatorService service;

	private LocalDate dueDate;
	private LocalDate today;
	private LocalDate expiredDueDate;
	private Bankslip bankslipYahoo;

	@Before
	public void setup() {
		dueDate = LocalDate.of(2018, 10, 15);
		today = LocalDate.of(2018, 10, 1);
		bankslipYahoo = getBankslip();
	}
	
	@Test
	public void givenANotExpiredBankslip_thenReturnEmpty() {
		service.calculateTo(bankslipYahoo, today);
		assertThat(bankslipYahoo.getFine()).isNull();
	}

	@Test
	public void givenAnExpiredBankslipLessThenTenDays_thenReturnCalculatedFine() {
		expiredDueDate = LocalDate.of(2018, 10, 20);
		service.calculateTo(bankslipYahoo, expiredDueDate);
		assertThat(bankslipYahoo.getFine()).isEqualByComparingTo(new BigDecimal(25));
	}

	@Test
	public void givenAnExpiredBankslipEqualTenDays_thenReturnCalculatedFine() {
		expiredDueDate = LocalDate.of(2018, 10, 25);
		service.calculateTo(bankslipYahoo, expiredDueDate);
		assertThat(bankslipYahoo.getFine()).isEqualByComparingTo(new BigDecimal(50));
	}

	@Test
	public void givenAnExpiredBankslipGreaterThenTenDays_thenReturnCalculatedFine() {
		expiredDueDate = LocalDate.of(2018, 10, 26);
		service.calculateTo(bankslipYahoo, expiredDueDate);
		assertThat(bankslipYahoo.getFine()).isEqualByComparingTo(new BigDecimal(110));
	}

	private Bankslip getBankslip() {
		return Bankslip.builder()
				.customer("Yahoo")
				.dueDate(dueDate)
				.totalInCents(new BigDecimal(1000.0))
				.build();
	}
}
