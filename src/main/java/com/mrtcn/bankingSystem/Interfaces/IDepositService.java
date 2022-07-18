package com.mrtcn.bankingSystem.Interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.DepositRequest;

public interface IDepositService {
	public ResponseEntity<Account> deposit(String accountNumber, DepositRequest request, KafkaTemplate<String,String> producer);
}
