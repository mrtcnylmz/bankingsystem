package com.mrtcn.bankingSystem.Services;

import com.mrtcn.bankingSystem.Interfaces.ICreateAccountService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.ZonedDateTime;

@Component
public class CreateAccountService implements ICreateAccountService{
	
	@Override
    public ResponseEntity<AccountCreateResponse> createAccount(NewAccountRequest request){

        //Account type checks made here.
        if (!(request.getType().equals("TL") || request.getType().equals("Dolar") || request.getType().equals("Altın"))){
            AccountCreateResponse result = AccountCreateResponse.builder()
                    .message("Invalid Account Type: " + request.getType())
                    .build();
            return ResponseEntity
                    .badRequest()
                    .header("content-type","application/json")
                    .body(result);
        }

        //10-Digit Random number for account number.
        long randomNumber = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        Account acc = Account.builder()
                .number(randomNumber)
                .type(request.getType())
                .email(request.getEmail())
                .tc(request.getTc())
                .name(request.getName())
                .surname(request.getSurname())
                .lastUpdate(ZonedDateTime.now().toInstant())
                .build();

        //Accounts folder.
        File accountsFolder = new File("accounts");
        if (!accountsFolder.exists()){
            accountsFolder.mkdir();
        }

        //Created account gets written to file inside the accounts folder.
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("accounts/" + randomNumber)));
            os.writeObject(acc);
            os.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //A response for json response body.
        AccountCreateResponse result = AccountCreateResponse.builder()
                .message("Account Created")
                .accountNumber(randomNumber)
                .build();

        return ResponseEntity
                .created(null)
                .header("content-type","application/json")
                .body(result);
    }
}
