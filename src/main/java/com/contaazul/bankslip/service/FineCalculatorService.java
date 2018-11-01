package com.contaazul.bankslip.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.contaazul.bankslip.model.Bankslip;

@Service
public class FineCalculatorService {

	private static final int MAX_DAYS = 10;
	private static final BigDecimal FINE_ONE_PERCENT = new BigDecimal("0.01");
	private static final BigDecimal FINE_HALF_PERCENT = new BigDecimal("0.005");
	BigDecimal fine;

	public BigDecimal calculate(Bankslip bankslip, LocalDate today) {
		BigDecimal totalInCents = bankslip.getTotalInCents();
		LocalDate dueDate = bankslip.getDueDate();

		long days = getNumberOfDays(dueDate, today);

		if (days == 0)
			return new BigDecimal(0);

		if (days <= MAX_DAYS) {
			fine = FINE_HALF_PERCENT;
		} else {
			fine = FINE_ONE_PERCENT;
		}
		return fine.multiply(totalInCents).multiply(new BigDecimal(days));
	}

	private long getNumberOfDays(LocalDate dueDate, LocalDate today) {
		long days = ChronoUnit.DAYS.between(dueDate, today);
		return days > 0 ? days : 0;
	}

}
