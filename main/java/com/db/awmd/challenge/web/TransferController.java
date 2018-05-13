package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NegativeAccountBalanceException;
import com.db.awmd.challenge.service.AccountsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransferController {

  private final AccountsService accountsService;
  
  @Autowired
  public TransferController(AccountsService accountsService ) {
    this.accountsService = accountsService;
    
  }

   
 /**
  * Transfer amount from one account to
  * another account
  * @param accountFromId
  * @param accountToId
  * @param balance
  * @return
  */
  
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> transferMoney(
		  @RequestBody @Valid Transfer transfer ) {
  

    try {
    this.accountsService.transferMoney(transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getTransferAmount());
    } catch (NegativeAccountBalanceException nabe) {
      return new ResponseEntity<>(nabe.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }
  
  
  
}
