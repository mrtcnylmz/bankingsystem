package com.mrtcn.bankingSystem.Interfaces;

import org.springframework.http.ResponseEntity;

import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;

public interface ICreateAccountService {
	public ResponseEntity<AccountCreateResponse> createAccount(NewAccountRequest request);
}
