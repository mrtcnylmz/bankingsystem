package com.mrtcn.bankingSystem.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.IDepositService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.DepositRequest;

@Component
public class DepositService implements IDepositService {

	@Override
	public ResponseEntity<Account> deposit(String accountNumber, DepositRequest request, KafkaTemplate<String,String> producer) {

		//File lookout for account file.
		File accountFile = new File("accounts/" + accountNumber);
		if (accountFile.exists()){
			try{
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(accountFile));
				Account account = (Account)is.readObject(); //Found account data got serialized to be used.
				is.close();

				account.setBalance(account.getBalance() + request.getAmount()); //New balance set after deposit.
				account.setLastUpdate(ZonedDateTime.now().toInstant()); //Accounts last modification.

				//After its processes are done updated account data gets written to file.
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(accountFile));
				os.writeObject(account);
				os.close();

				//Log string for keeping track of processes.
				String log =
						account.getNumber() + " " +
						"deposit" + " " +
						"amount:" + " " +
						request.getAmount() + " " +
						account.getType();

				//Log send to kafka.
				producer.send("logs",log);
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
