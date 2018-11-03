package com.contaazul.bankslip.model;

public interface StatusEvent {
	void pay(Bankslip bankslip);
	void cancel(Bankslip bankslip);
}
