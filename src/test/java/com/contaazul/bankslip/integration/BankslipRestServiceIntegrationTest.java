package com.contaazul.bankslip.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.contaazul.BankslipApplication;
import com.contaazul.bankslip.dto.Payment;
import com.contaazul.bankslip.model.Bankslip;
import com.contaazul.bankslip.model.Status;
import com.contaazul.bankslip.repository.BankslipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BankslipApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class BankslipRestServiceIntegrationTest {

	@Autowired
    private MockMvc mvc;

    @Autowired
    private BankslipRepository repository;
    
    @Autowired
	private ObjectMapper mapper;

    @After
    public void resetDb() {
        repository.deleteAll();
    }
    
    
    @Test
    public void whenValidBankslip_thenCreateBankslip() throws IOException, Exception {
    	LocalDate dueDate = LocalDate.of(2018, 10, 15);
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(dueDate).totalInCents(new BigDecimal(1000)).build();
    	
        mvc.perform(post("/bankslips")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(mapper.writeValueAsString(bankslipYahoo)));

        List<Bankslip> found = repository.findAll();
        
        assertThat(found).extracting(Bankslip::getCustomer).containsOnly("Yahoo");
        assertThat(found).extracting(Bankslip::getStatus).containsOnly(Status.PENDING);
    }
    
    @Test
    public void whenInValidBankslip_thenReturnInvalid() throws IOException, Exception {
    	LocalDate dueDate = LocalDate.of(2018, 10, 15);
    	Bankslip bankslipYahoo = Bankslip.builder().customer("")
				.dueDate(dueDate).totalInCents(new BigDecimal(1000)).build();
    	
        mvc.perform(post("/bankslips")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(mapper.writeValueAsString(bankslipYahoo)))
			        .andExpect(status().isUnprocessableEntity())
					.andExpect(jsonPath("$.msg", is("Invalid bankslip provided.The possible reasons are: A field of the provided bankslip was null or with invalid values")));

        List<Bankslip> found = repository.findAll();
        
        assertThat(found).isEmpty();
    }
    
    @Test
    public void whenPostWithoutBankslip_thenReturnInvalid() throws IOException, Exception {
    	
        mvc.perform(post("/bankslips")
        			.contentType(MediaType.APPLICATION_JSON))
			        .andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.msg", is("Bankslip not provided in the request body")));

        List<Bankslip> found = repository.findAll();
        
        assertThat(found).isEmpty();
    }
    
    @Test
    public void whenFindAllBankslips_thenReturnJsonArray() throws Exception {
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(LocalDate.of(2018, 10, 15)).totalInCents(new BigDecimal("1000.00")).build();
    	Bankslip bankslipGoogle = Bankslip.builder().customer("Google")
				.dueDate(LocalDate.of(2018, 10, 15)).totalInCents(new BigDecimal("2000.00")).build();
    	
    	List<Bankslip> list = Arrays.asList(bankslipYahoo, bankslipGoogle);

    	createTestBankslip(list);
        
        mvc.perform(get("/bankslips"))
        			.andExpect(status().isOk())
        			.andExpect(jsonPath("$", hasSize(2)))
        			.andExpect(jsonPath("$[0].customer", is(bankslipYahoo.getCustomer())))
        			.andExpect(jsonPath("$[0].total_in_cents", comparesEqualTo(bankslipYahoo.getTotalInCents().doubleValue())))
        			.andExpect(jsonPath("$[0].due_date", is(bankslipYahoo.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
        			
        			.andExpect(jsonPath("$[1].customer", is(bankslipGoogle.getCustomer())))
        			.andExpect(jsonPath("$[1].total_in_cents", comparesEqualTo(bankslipGoogle.getTotalInCents().doubleValue())))
        			.andExpect(jsonPath("$[1].due_date", is(bankslipGoogle.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))));
    }

    @Test
    public void whenFindAllBankslips_thenReturnEmptyJsonArray() throws Exception {
    	mvc.perform(get("/bankslips"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    public void whenFindByIdWithNoExpirationDueDate_thenReturnBankslipWithoutFine() throws Exception {
    	LocalDate today = LocalDate.now().plusDays(10L);
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(today).totalInCents(new BigDecimal("1000.00")).build();
    	
    	repository.saveAndFlush(bankslipYahoo);
    	
    	List<Bankslip> bankslips = repository.findAll();
    	Bankslip bankslipDb = bankslips.get(0);
    	
    	mvc.perform(get("/bankslips/" + bankslipDb.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.customer", is(bankslipDb.getCustomer())))
			.andExpect(jsonPath("$", not(hasKey("fine"))));
    }
    
    @Test
    public void whenFindByIdWithDueDateEqualsFiveDays_thenReturnBankslipWithFine() throws Exception {
    	LocalDate today = LocalDate.now().minusDays(5L);
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(today).totalInCents(new BigDecimal("1000.00")).build();
    	
    	repository.saveAndFlush(bankslipYahoo);
    	
    	List<Bankslip> bankslips = repository.findAll();
    	Bankslip bankslipDb = bankslips.get(0);
    	
    	mvc.perform(get("/bankslips/" + bankslipDb.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.customer", is(bankslipDb.getCustomer())))
			.andExpect(jsonPath("$.fine", comparesEqualTo(new Double("25.0"))));
    }
    
    
    @Test
    public void whenFindByIdWithDueDateEqualsTenDays_thenReturnBankslipWithFine() throws Exception {
    	LocalDate today = LocalDate.now().minusDays(10L);
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(today).totalInCents(new BigDecimal("1000.00")).build();
    	
    	repository.saveAndFlush(bankslipYahoo);
    	
    	List<Bankslip> bankslips = repository.findAll();
    	Bankslip bankslipDb = bankslips.get(0);
    	
    	mvc.perform(get("/bankslips/" + bankslipDb.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.customer", is(bankslipDb.getCustomer())))
			.andExpect(jsonPath("$.fine", comparesEqualTo(new Double("50.0"))));
    }
    
    @Test
    public void whenFindByIdWithDueDateEqualsElevenDays_thenReturnBankslipWithFine() throws Exception {
    	LocalDate today = LocalDate.now().minusDays(11L);
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(today).totalInCents(new BigDecimal("1000.00")).build();
    	
    	repository.saveAndFlush(bankslipYahoo);
    	
    	List<Bankslip> bankslips = repository.findAll();
    	Bankslip bankslipDb = bankslips.get(0);
    	
    	mvc.perform(get("/bankslips/" + bankslipDb.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.customer", is(bankslipDb.getCustomer())))
			.andExpect(jsonPath("$.fine", comparesEqualTo(new Double("110.0"))));
    }
    
    @Test
    public void whenFindByIdWithWrongId_thenReturnNoContent() throws Exception {
    	mvc.perform(get("/bankslips/e8b80240-ea18-4d1b-a1ee-66b0b60d9fc9"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.msg", is("Bankslip not found with the specified id")));
    }
    
    @Test
    public void whenPayBankslip_thenReturnPaid() throws Exception {
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(LocalDate.of(2018, 10, 15)).totalInCents(new BigDecimal("1000.00")).build();
    	Payment payment = Payment.builder().paymentDate(LocalDate.of(2018, 10, 15)).build();
    	
    	repository.saveAndFlush(bankslipYahoo);
    	
    	List<Bankslip> bankslips = repository.findAll();
    	Bankslip bankslipDb = bankslips.get(0);
    	
    	mvc.perform(post("/bankslips/" + bankslipDb.getId()+ "/payments")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(mapper.writeValueAsString(payment)))
			.andExpect(status().isNoContent());
    	
    	bankslips = repository.findAll();
    	assertThat(bankslips).extracting(Bankslip::getStatus).containsOnly(Status.PAID);
    }
    
    @Test
    public void whenPayBankslipWithWrongId_thenNotFound() throws Exception {
    	Payment payment = Payment.builder().paymentDate(LocalDate.of(2018, 10, 15)).build();
    	mvc.perform(post("/bankslips/e8b80240-ea18-4d1b-a1ee-66b0b60d9fc9/payments")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(mapper.writeValueAsString(payment)))
			.andExpect(status().isNotFound());
    }
    
    @Test
    public void whenCancelBankslip_thenReturnCanceled() throws Exception {
    	
    	Bankslip bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(LocalDate.of(2018, 10, 15)).totalInCents(new BigDecimal("1000.00")).build();
    	
    	repository.saveAndFlush(bankslipYahoo);
    	
    	List<Bankslip> bankslips = repository.findAll();
    	Bankslip bankslipDb = bankslips.get(0);
    	
    	mvc.perform(delete("/bankslips/" + bankslipDb.getId()))
			.andExpect(status().isNoContent());
    	
    	bankslips = repository.findAll();
    	assertThat(bankslips).extracting(Bankslip::getStatus).containsOnly(Status.CANCELED);
    }
    
    
    @Test
    public void whenCancelBankslipWithWrongId_thenReturnNotFound() throws Exception {
    	mvc.perform(delete("/bankslips/e8b80240-ea18-4d1b-a1ee-66b0b60d9fc9"))
			.andExpect(status().isNotFound());
    }

	private void createTestBankslip(List<Bankslip> list) {
		list.forEach(bankslip->{
			repository.saveAndFlush(bankslip);
		});
		
	}
}
