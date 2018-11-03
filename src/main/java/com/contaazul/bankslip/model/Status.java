package com.contaazul.bankslip.model;

import com.contaazul.bankslip.exception.InvalidStatusException;

public enum Status implements StatusEvent {

	PENDING {

		@Override
		public void pay(Bankslip bankslip) {
			bankslip.setStatus(PAID);
		}

		@Override
		public void cancel(Bankslip bankslip) {
			bankslip.setStatus(CANCELED);
		}
	},
	PAID {
		@Override
		public void pay(Bankslip bankslip) {
			throw new InvalidStatusException("Bankslip is already paid");
		}

		@Override
		public void cancel(Bankslip bankslip) {
			throw new InvalidStatusException("Bankslip is already paid");
		}
	},
	CANCELED {
		@Override
		public void pay(Bankslip bankslip) {
			throw new InvalidStatusException("Bankslip is already canceled");
		}

		@Override
		public void cancel(Bankslip bankslip) {
			throw new InvalidStatusException("Bankslip is already canceled");
		}
	};
}
