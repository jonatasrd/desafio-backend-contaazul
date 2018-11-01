package com.contaazul.bankslip.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.contaazul.bankslip.model.Bankslip;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BankslipRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BankslipRepository repository;

	private LocalDate dueDate;
	private Bankslip bankslipYahoo;
	private Bankslip bankslipGoogle;
	private Bankslip bankslipOracle;
	private UUID id;

	@Before
	public void setup() {
		id = UUID.fromString("13ab343b-afdb-4b0a-8762-94eaf64602c6");
		dueDate = LocalDate.of(2018, 10, 1);
		bankslipYahoo = Bankslip.builder().customer("Yahoo").dueDate(dueDate)
				.totalInCents(new BigDecimal(1000)).build();
		bankslipGoogle = Bankslip.builder().customer("Google").dueDate(dueDate)
				.totalInCents(new BigDecimal(2000)).build();
		bankslipOracle = Bankslip.builder().customer("Oracle").dueDate(dueDate)
				.totalInCents(new BigDecimal(3000)).build();
	}

	@Test
	public void whenFindById_thenReturnBankslip() {
		entityManager.persistAndFlush(bankslipYahoo);
		Bankslip fromDb = repository.findById(bankslipYahoo.getId())
				.orElse(null);
		assertThat(fromDb.getCustomer()).isEqualTo(bankslipYahoo.getCustomer());
	}

	@Test
	public void whenInvalidId_thenReturnNull() {
		Bankslip fromDb = repository.findById(id).orElse(null);
		assertThat(fromDb).isNull();
	}

	@Test
	public void givenSetOfBankslips_whenFindAll_thenReturnAllBankslips() {

		entityManager.persist(bankslipYahoo);
		entityManager.persist(bankslipGoogle);
		entityManager.persist(bankslipOracle);

		entityManager.flush();

		List<Bankslip> allEmployees = repository.findAll();

		assertThat(allEmployees).hasSize(3).extracting(Bankslip::getCustomer)
				.containsOnly(bankslipYahoo.getCustomer(),
						bankslipGoogle.getCustomer(),
						bankslipOracle.getCustomer());
	}
}
