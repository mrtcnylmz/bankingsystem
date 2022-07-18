package com.mrtcn.bankingSystem.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.IGetAccountService;
import com.mrtcn.bankingSystem.Models.Account;

@Component
public class GetAccountService implements IGetAccountService {

	@Override
	public ResponseEntity<Account> getAccount(String accountNumber) {
		
		//File lookout for account file.
		File accountFile = new File("accounts/" + accountNumber);
		if (accountFile.exists()){
			try{
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(accountFile));
				Account account = (Account)is.readObject(); //Found account data got deserialized to be used as json response.
				is.close();
				return ResponseEntity
						.ok()
						.header("content-type","application/json")
						.lastModified(account.getLastUpdate())
						.body(account);
			} catch(IOException | ClassNotFoundException e){
				e.printStackTrace();
			}
		}

		//If account number couldn't be found in files, a 404 response returns.
		return ResponseEntity
				.notFound()
				.header("content-type","application/json")
				.build();
	}
}
