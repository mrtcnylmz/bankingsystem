package com.mrtcn.bankingSystem.Interfaces;

import org.springframework.http.ResponseEntity;
import com.mrtcn.bankingSystem.Models.Account;

public interface IGetAccountService {
	public ResponseEntity<Account> getAccount(String accountNumber);

}
