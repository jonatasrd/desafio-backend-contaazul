package com.contaazul.bankslip.api.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.contaazul.bankslip.dto.Payment;
import com.contaazul.bankslip.exception.ResourceNotFoundException;
import com.contaazul.bankslip.model.Bankslip;
import com.contaazul.bankslip.service.BankslipServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(BankslipRestService.class)
public class BankslipRestServiceTest {

	@Autowired
    private MockMvc mvc;
 
    @MockBean
    private BankslipServiceImpl service;
    
	@Autowired
	private ObjectMapper mapper;
    
    private LocalDate dueDate;
    private UUID id = UUID.fromString("13ab343b-afdb-4b0a-8762-94eaf64602c6");
    private Bankslip bankslipYahoo;
    private Bankslip bankslipGoogle;
    private Bankslip bankslipOracle;
    private Payment payment;
    
    @Before
    public void setUp() throws Exception {
    	dueDate = LocalDate.of(2018, 10, 15);
    	payment = Payment.builder().paymentDate(LocalDate.of(2018, 10, 15)).build();
    	bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(dueDate).totalInCents(new BigDecimal(1000)).build();
    	bankslipGoogle = Bankslip.builder().customer("Google")
				.dueDate(dueDate).totalInCents(new BigDecimal(2000)).build();
    	bankslipOracle = Bankslip.builder().customer("Oracle")
				.dueDate(dueDate).totalInCents(new BigDecimal(3000)).build();
    }
    
    @Test
    public void whenPostBankslip_thenCreateBankslip() throws Exception {
        given(service.save(Mockito.any())).willReturn(bankslipYahoo);

        mvc.perform(post("/bankslips")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(mapper.writeValueAsString(bankslipYahoo)))
        			.andExpect(status().isCreated())
        			.andExpect(jsonPath("$.customer", is("Yahoo")));
        
        verify(service, VerificationModeFactory.times(1)).save(Mockito.any());
        reset(service);
    }
    
    @Test
    public void whenPostBankslipWithoutBankslip_shouldReturnBadRequest() throws Exception {
        given(service.save(Mockito.any())).willReturn(null);

        mvc.perform(post("/bankslips")
        			.contentType(MediaType.APPLICATION_JSON))
        			.andExpect(status().isBadRequest())
        			.andExpect(jsonPath("$.msg", is("Bankslip not provided in the request body")));
        
        verify(service, VerificationModeFactory.times(0)).save(Mockito.any());
        reset(service);
    }
    
    @Test
    public void whenPostWrongBankslip_shouldReturnUnprocessableEntity() throws Exception {
        given(service.save(Mockito.any())).willReturn(null);

        bankslipYahoo.setCustomer("");
        
        mvc.perform(post("/bankslips")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(mapper.writeValueAsString(bankslipYahoo)))
        			.andExpect(status().isUnprocessableEntity())
        			.andExpect(jsonPath("$.msg", is("Invalid bankslip provided.The possible reasons are: A field of the provided bankslip was null or with invalid values")));
        
        verify(service, VerificationModeFactory.times(0)).save(Mockito.any());
        reset(service);
    }
    
    @Test
    public void whenFindAllBankslips_thenReturnJsonArray() throws Exception {

        List<Bankslip> allBankslips = Arrays.asList(bankslipYahoo, bankslipGoogle, bankslipOracle);

        given(service.findAll()).willReturn(allBankslips);

        mvc.perform(get("/bankslips"))
        			.andExpect(status().isOk())
        			.andExpect(jsonPath("$", hasSize(3)))
        			.andExpect(jsonPath("$[0].customer", is(bankslipYahoo.getCustomer())))
        			.andExpect(jsonPath("$[0].total_in_cents", comparesEqualTo(bankslipYahoo.getTotalInCents().intValue())))
        			.andExpect(jsonPath("$[0].due_date", is(bankslipYahoo.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
        			
        			.andExpect(jsonPath("$[1].customer", is(bankslipGoogle.getCustomer())))
        			.andExpect(jsonPath("$[1].total_in_cents", comparesEqualTo(bankslipGoogle.getTotalInCents().intValue())))
        			.andExpect(jsonPath("$[1].due_date", is(bankslipGoogle.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
        			
        			.andExpect(jsonPath("$[2].customer", is(bankslipOracle.getCustomer())))
        			.andExpect(jsonPath("$[2].total_in_cents", comparesEqualTo(bankslipOracle.getTotalInCents().intValue())))
        			.andExpect(jsonPath("$[2].due_date", is(bankslipOracle.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))));
        
        verify(service, VerificationModeFactory.times(1)).findAll();
        reset(service);
    }
    
    @Test
    public void whenFindBankslipById_thenReturnOk() throws Exception {

    	given(service.find(id)).willReturn(bankslipGoogle);
    	
    	 mvc.perform(get("/bankslips/" + id))
     			.andExpect(status().isOk())
     			.andExpect(jsonPath("$.customer", is(bankslipGoogle.getCustomer())))
     			.andExpect(jsonPath("$.total_in_cents", is(bankslipGoogle.getTotalInCents().intValue())))
     			.andExpect(jsonPath("$.due_date", is(bankslipGoogle.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))));
    	 
         verify(service, VerificationModeFactory.times(1)).find(id);
         reset(service);
    }
    
    @Test
    public void whenFindByIdWithWrongId_thenReturnNotFound() throws Exception {

    	given(service.find(id)).willThrow(ResourceNotFoundException.class);
    	
    	 mvc.perform(get("/bankslips/" + id))
     			.andExpect(status().isNotFound())
     			.andExpect(jsonPath("$.msg", is("Bankslip not found with the specified id")));
    	 
         verify(service, VerificationModeFactory.times(1)).find(id);
         reset(service);
    }
    
    @Test
    public void whenPayBankslip_thenReturnNoContent() throws Exception {

    	given(service.pay(id, payment)).willReturn(bankslipGoogle);
    	
    	mvc.perform(post("/bankslips/" + id + "/payments")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(mapper.writeValueAsString(payment)))
    			.andExpect(status().isNoContent());
    	 
         verify(service, VerificationModeFactory.times(1)).pay(id, payment);
         reset(service);
    }
    
    
    @Test
    public void whenPayBankslipWithWrongId_thenReturnNotFound() throws Exception {

    	given(service.pay(id, payment)).willThrow(ResourceNotFoundException.class);
    	
    	mvc.perform(post("/bankslips/" + id + "/payments")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(mapper.writeValueAsString(payment)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.msg", is("Bankslip not found with the specified id")));
    	 
         verify(service, VerificationModeFactory.times(1)).pay(id, payment);
         reset(service);
    }
    
    @Test
    public void whenCancelBankslip_thenReturnNoContent() throws Exception {

    	given(service.cancel(id)).willReturn(bankslipGoogle);
    	
    	mvc.perform(delete("/bankslips/" + id))
    			.andExpect(status().isNoContent());
    	 
         verify(service, VerificationModeFactory.times(1)).cancel(id);
         reset(service);
    }
    
    @Test
    public void whenCancelBankslipWithWrongId_thenReturnNotFound() throws Exception {

    	given(service.cancel(id)).willThrow(ResourceNotFoundException.class);
    	
    	mvc.perform(delete("/bankslips/" + id))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.msg", is("Bankslip not found with the specified id")));
    	 
         verify(service, VerificationModeFactory.times(1)).cancel(id);
         reset(service);
    }
}
