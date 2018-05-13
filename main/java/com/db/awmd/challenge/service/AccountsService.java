package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.NegativeAccountBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  @Getter
  private final EmailNotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository,EmailNotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public void transferMoney(String accountFromId, String accountToId ,BigDecimal amtToTransfer) throws NegativeAccountBalanceException{
	 
	  
	  BigDecimal updatedFromAccountBalance = this.accountsRepository.getAccount(accountFromId).getBalance().subtract(amtToTransfer,new MathContext(2));
	  if(updatedFromAccountBalance.signum() == -1)
		  throw new NegativeAccountBalanceException("From Account balance cannot be negative");
	  
	  BigDecimal updatedToAccountBalance = this.accountsRepository.getAccount(accountToId).getBalance().add(amtToTransfer);
	  
	  this.accountsRepository.getAccount(accountFromId).setBalance(updatedFromAccountBalance);
	  this.accountsRepository.getAccount(accountToId).setBalance(updatedToAccountBalance);
	  
	  notificationService.notifyAboutTransfer(this.accountsRepository.getAccount(accountFromId), "Amount: " + amtToTransfer + " transferred to " + accountToId  );
	  notificationService.notifyAboutTransfer(this.accountsRepository.getAccount(accountToId), "Amount: " + amtToTransfer + " transferred from " + accountFromId  );
  }
}
