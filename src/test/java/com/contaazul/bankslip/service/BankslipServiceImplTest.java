package com.contaazul.bankslip.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.contaazul.bankslip.dto.Payment;
import com.contaazul.bankslip.exception.ResourceNotFoundException;
import com.contaazul.bankslip.model.Bankslip;
import com.contaazul.bankslip.model.Status;
import com.contaazul.bankslip.repository.BankslipRepository;

@RunWith(SpringRunner.class)
public class BankslipServiceImplTest {

	@TestConfiguration
	static class BankslipServiceImplTestContextConfiguration {
		
		@Bean
		public BankslipService bankslipService() {
			return new BankslipServiceImpl(new FineCalculatorService());
		}
	}

	@Autowired
	BankslipService service;

	@MockBean
	private BankslipRepository repository;

	private LocalDate dueDate;
	private UUID id = UUID.fromString("13ab343b-afdb-4b0a-8762-94eaf64602c6");
	private UUID idNotFund = UUID.fromString("13ab343b-afdb-4b0a-8762-94eaf64602c7");
	private Bankslip bankslipYahoo;
	private Bankslip bankslipGoogle;
	private Bankslip bankslipOracle;
	private Payment payment;

	@Before
	public void setUp() {
		dueDate = LocalDate.of(2018, 10, 15);
		payment = Payment.builder().paymentDate(LocalDate.of(2018, 10, 15)).build();
		bankslipYahoo = Bankslip.builder().id(id).customer("Yahoo")
				.dueDate(dueDate).totalInCents(new BigDecimal(1000)).status(Status.PENDING).build();
		
		bankslipGoogle = Bankslip.builder().customer("Google")
				.dueDate(dueDate).totalInCents(new BigDecimal(2000)).status(Status.PENDING).build();
		
		bankslipOracle = Bankslip.builder().customer("Oracle")
				.dueDate(dueDate).totalInCents(new BigDecimal(3000)).status(Status.PENDING).build();
		
		List<Bankslip> allBankslips = Arrays.asList(bankslipYahoo, bankslipGoogle, bankslipOracle);
		
		 Mockito.when(repository.save(bankslipGoogle)).thenReturn(bankslipGoogle);
		 Mockito.when(repository.findById(bankslipYahoo.getId())).thenReturn(Optional.of(bankslipYahoo));
		 Mockito.when(repository.findAll()).thenReturn(allBankslips);
		 Mockito.when(repository.findById(idNotFund)).thenThrow(ResourceNotFoundException.class);
	}
	
	
	@Test
    public void whenSaveBankslip_thenBankslipShouldBeReturned() {
		Bankslip fromDb = service.save(bankslipGoogle);
        assertThat(fromDb.getCustomer()).isEqualTo("Google");

        verifySaveIsCalledOnce();
    }
	
	@Test
    public void whenValidId_thenBankslipShouldBeFound() {
		Bankslip fromDb = service.find(id);
        assertThat(fromDb.getCustomer()).isEqualTo("Yahoo");
        
        verifyFindByIdIsCalledOnce();
    }
	
	@Test(expected = ResourceNotFoundException.class)
    public void whenInvalidId_thenThrowResourceNotFoundException() {
		Bankslip fromDb = service.find(idNotFund);
        assertThat(fromDb).isNull();
        
        verifyFindByIdIsCalledOnce();
    }
	
	@Test
    public void whenPayBankslip_thenChangeStatus() {
		Bankslip fromDb = service.pay(id, payment);
        assertThat(fromDb.getCustomer()).isEqualTo("Yahoo");
        assertThat(fromDb.getStatus().name()).isEqualTo("PAID");
        assertThat(fromDb.getPaymentDate()).isEqualTo(payment.getPaymentDate());
        
        verifyFindByIdIsCalledOnce();
    }
	
	@Test
    public void whenDeleteBankslip_thenChangeStatus() {
		Bankslip fromDb = service.cancel(id);
        assertThat(fromDb.getCustomer()).isEqualTo("Yahoo");
        assertThat(fromDb.getStatus().name()).isEqualTo("CANCELED");
        
        verifyFindByIdIsCalledOnce();
    }
	
	private void verifySaveIsCalledOnce() {
		Mockito.verify(repository, VerificationModeFactory.times(1)).save(Mockito.any());
        Mockito.reset(repository);
	}
	
	private void verifyFindByIdIsCalledOnce() {
        Mockito.verify(repository, VerificationModeFactory.times(1)).findById(Mockito.any());
        Mockito.reset(repository);
    }
	
}
