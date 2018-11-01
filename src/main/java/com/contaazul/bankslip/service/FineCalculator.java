package com.contaazul.bankslip.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

@Service
public class FineCalculator {

	private static final int MAX_DAYS = 10;
	private static final BigDecimal FINE_0_10 = new BigDecimal("0.01");
	private static final BigDecimal FINE_0_005 = new BigDecimal("0.005");
	BigDecimal fine;

	public BigDecimal calculate(BigDecimal totalInCents, LocalDate dueDate) {
		long days = getNumberOfDays(dueDate);

		System.out.println(days);

		if (days == 0)
			return new BigDecimal(0);

		if (days < MAX_DAYS) {
			fine = FINE_0_005;
		} else {
			fine = FINE_0_10;
		}
		return fine.multiply(totalInCents).multiply(new BigDecimal(days));
	}

	private long getNumberOfDays(LocalDate dueDate) {
		LocalDate today = LocalDate.now();
		int days = Period.between(dueDate, today).getDays();
		return days > 0 ? days : 0;
	}

}
