package com.mrtcn.bankingSystem.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.mrtcn.bankingSystem.Interfaces.ICreateAccountService;
import com.mrtcn.bankingSystem.Interfaces.IDepositService;
import com.mrtcn.bankingSystem.Interfaces.IGetAccountService;
import com.mrtcn.bankingSystem.Interfaces.ILogService;
import com.mrtcn.bankingSystem.Interfaces.ITransferService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.DepositRequest;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import com.mrtcn.bankingSystem.Responses.LogResult;
import com.mrtcn.bankingSystem.Responses.TransferResult;

@RestController
public class BankingController {
	
	@Autowired
	private ICreateAccountService createAccountService;
	
	@Autowired
	private IGetAccountService getAccountService;
	
	@Autowired
	private IDepositService depositService;
	
	@Autowired
	private ITransferService transferService;
	
	@Autowired
	private ILogService logService;

	@Autowired
	private KafkaTemplate<String,String> producer;

	//1
	//A service to create a user account.
	@RequestMapping(path = "account/register", method = RequestMethod.POST)
	public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody NewAccountRequest request){
		
		return this.createAccountService.createAccount(request); 
	}

	//2
	//A service to get account information from account number.
	@RequestMapping(path = "account/{accountNumber}", method = RequestMethod.GET)
	public ResponseEntity<Account> getAccount(@PathVariable String accountNumber){

		return this.getAccountService.getAccount(accountNumber);
	}

	//3
	//A service that takes an account number and handles monetary deposits accordingly.
    @RequestMapping(path = "/account/{accountNumber}", method = RequestMethod.POST)
	public ResponseEntity<Account> deposit(@PathVariable String accountNumber, @RequestBody DepositRequest request) {
    	
    	return this.depositService.deposit(accountNumber, request, producer);
	}

	//4
	//A service to make balance transfers between accounts.
	@RequestMapping(path = "/account/{accountNumber}", method = RequestMethod.PATCH)
	public ResponseEntity<TransferResult> transfer(@PathVariable String accountNumber, @RequestBody TransferRequest request) {
		
		return this.transferService.transfer(accountNumber, request, producer);
	}

	//5
	//A service to access logs received and logged by kafka.
	@CrossOrigin(origins = {"http://localhost:6162"})
	@RequestMapping(path = "account/{accountNumber}/logs", method = RequestMethod.GET)
	public ResponseEntity<List<LogResult>> getLog(@PathVariable String accountNumber){

		return this.logService.getLog(accountNumber);
	}
}
