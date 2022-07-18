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

import com.mrtcn.bankingSystem.Interfaces.ITransferService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.TransferResult;

@Component
public class TransferService implements ITransferService{

	@Override
	public ResponseEntity<TransferResult> transfer(String accountNumber, TransferRequest request, KafkaTemplate<String, String> producer) {
		TransferResult result = TransferResult.builder().build();

		//File lookout for account files.
		File senderAccountFile = new File("accounts/" + accountNumber);
		File receiverAccountFile = new File("accounts/" + request.getTransferredAccountNumber());

		//If either of the files are missing a 404 response returns.
		if (!(senderAccountFile.exists() && receiverAccountFile.exists())) {
			return ResponseEntity
					.notFound()
					.header("content-type", "application/json")
					.build();
		}

		Account senderAccount;
		Account receiverAccount;
		//Account files read from files.
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(senderAccountFile));
			ObjectInputStream ir = new ObjectInputStream(new FileInputStream(receiverAccountFile));

			//Files deserialized into Account model.
			senderAccount = (Account) is.readObject();
			receiverAccount = (Account) ir.readObject();

			is.close();
			ir.close();

		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		//Check to see if client has enough balance for transfer / inputted positive value.
		if (senderAccount.getBalance() < request.getAmount() || request.getAmount() <= 0) {
			result.setMessage("Insufficient/Invalid balance");
			return ResponseEntity
					.badRequest()
					.header("content-type", "application/json")
					.body(result);
		}

		//If sender and receiver accounts are in different types, an exchange service is necessary.
		if (!(senderAccount.getType().equals(receiverAccount.getType()))){
			senderAccount.setBalance(senderAccount.getBalance() - request.getAmount());	//Sender account update.
			senderAccount.setLastUpdate(ZonedDateTime.now().toInstant());

			//Receiver receives balance according to send amount and current exchange rate.
			receiverAccount.setBalance(receiverAccount.getBalance() + (int)(request.getAmount() * (new CurrencyExchange().Exchange(senderAccount,receiverAccount))));
			receiverAccount.setLastUpdate(ZonedDateTime.now().toInstant());

			//Account files updated.
			try{
				ObjectOutputStream sos = new ObjectOutputStream(new FileOutputStream(senderAccountFile));
				ObjectOutputStream ros = new ObjectOutputStream(new FileOutputStream(receiverAccountFile));

				sos.writeObject(senderAccount);
				ros.writeObject(receiverAccount);

				sos.close();
				ros.close();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			//Log string for keeping a log of processes.
			String log =
					senderAccount.getNumber() + " " +
					"transfer_amount:" + " " +
					request.getAmount() + " " + 
					"transferred_account:" + " " +
					receiverAccount.getNumber() + " " +
					senderAccount.getType();

			//Log sent to kafka.
			producer.send("logs", log);

			result.setMessage(
					"Transferred Successfully. " +
					request.getAmount() + " " + senderAccount.getType() + " to " +
					"~" + (int)(request.getAmount() * (new CurrencyExchange().Exchange(senderAccount,receiverAccount))) + " " + receiverAccount.getType()
			);
			return ResponseEntity
					.ok()
					.header("content-type", "application/json")
					.body(result);
		}

		//If sender and receiver share the same account type.
		senderAccount.setBalance(senderAccount.getBalance() - request.getAmount());
		senderAccount.setLastUpdate(ZonedDateTime.now().toInstant());

		receiverAccount.setBalance(receiverAccount.getBalance() + request.getAmount());
		receiverAccount.setLastUpdate(ZonedDateTime.now().toInstant());

		//Account files updated.
		try{
			ObjectOutputStream sos = new ObjectOutputStream(new FileOutputStream(senderAccountFile));
			ObjectOutputStream ros = new ObjectOutputStream(new FileOutputStream(receiverAccountFile));

			sos.writeObject(senderAccount);
			ros.writeObject(receiverAccount);

			sos.close();
			ros.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//Log string for keeping a log of processes.
		String log =
				senderAccount.getNumber() + " " +
				"transfer_amount:" + " " +
				request.getAmount() + " " + 
				"transferred_account:" + " " +
				receiverAccount.getNumber() + " " +
				senderAccount.getType();

		//Log sent to kafka.
		producer.send("logs", log);

		result.setMessage("Transferred Successfully. ");
		return ResponseEntity
				.ok()
				.header("content-type", "application/json")
				.body(result);
	}

}
