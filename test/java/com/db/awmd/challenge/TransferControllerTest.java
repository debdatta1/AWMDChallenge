package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }


  @Test
  public void transferMoney() throws Exception {
    Account fromAccountId = new Account("FROM-123", new BigDecimal("200"));
    Account toAccountId = new Account("TO-123", new BigDecimal("400"));
    this.accountsService.createAccount(fromAccountId);
    this.accountsService.createAccount(toAccountId);
    
    this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
    		.content( " {\"accountFromId\": \"FROM-123\", \"accountToId\": \"TO-123\",\"transferAmount\":100 }")).andExpect(status().isCreated());
    
    Account accountFrom = accountsService.getAccount("FROM-123");
    assertThat(accountFrom.getAccountId()).isEqualTo("FROM-123");
    assertThat(accountFrom.getBalance()).isEqualByComparingTo("100");
    
    
    Account accountTo = accountsService.getAccount("TO-123");
    assertThat(accountTo.getAccountId()).isEqualTo("TO-123");
    assertThat(accountTo.getBalance()).isEqualByComparingTo("500");
    
  }
  
  @Test
  public void createNegativeTransferAmount() throws Exception {
    Account fromAccountId = new Account("FROM-123", new BigDecimal("200.50"));
    Account toAccountId = new Account("TO-123", new BigDecimal("400.50"));
    this.accountsService.createAccount(fromAccountId);
    this.accountsService.createAccount(toAccountId);
    
    this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
    		.content( " {\"accountFromId\": \"FROM-123\", \"accountToId\": \"TO-123\",\"transferAmount\":-300 }")).andExpect(status().isBadRequest());
  }
  
  
  @Test
  public void createAccountNegativeBalanceAfterTransfer() throws Exception {
    Account fromAccountId = new Account("FROM-123", new BigDecimal("200.50"));
    Account toAccountId = new Account("TO-123", new BigDecimal("400.50"));
    this.accountsService.createAccount(fromAccountId);
    this.accountsService.createAccount(toAccountId);
    
    this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
    		.content( " {\"accountFromId\": \"FROM-123\", \"accountToId\": \"TO-123\",\"transferAmount\":400 }")).andExpect(status().isBadRequest());
  }
  
  
}
