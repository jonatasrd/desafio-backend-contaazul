package com.contaazul.bankslip.api.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private Bankslip bankslipYahoo;
    
    @Before
    public void setUp() throws Exception {
    	dueDate = LocalDate.of(2018, 10, 15);
    	bankslipYahoo = Bankslip.builder().customer("Yahoo")
				.dueDate(dueDate).totalInCents(new BigDecimal(1000)).build();
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
        			.andExpect(status().isBadRequest());
        
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
        			.andExpect(status().isUnprocessableEntity());
        
        verify(service, VerificationModeFactory.times(0)).save(Mockito.any());
        reset(service);
    }
}
