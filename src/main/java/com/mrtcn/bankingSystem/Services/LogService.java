package com.mrtcn.bankingSystem.Services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.ILogService;
import com.mrtcn.bankingSystem.Responses.LogResult;

@Component
public class LogService implements ILogService{

	@Override
	public ResponseEntity<List<LogResult>> getLog(String accountNumber) {

		List<LogResult> logList = new ArrayList<>();

		//Checks to see if any log file even exists.
		if (!(new File("logs").exists()) || !(new File("logs/logs.txt").exists()) ){
			return ResponseEntity
					.status(404)
					.header("content-type", "application/json")
					.body(null);
		}

		//Start reading log file.
		File logFile = new File("logs/logs.txt");
		try {
			Scanner myReader = new Scanner(logFile);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				String[] separated = data.split(" ");
				if(separated[0].equals(accountNumber)){
					if (separated[1].equals("transfer_amount:")){
						//Example:
						//2781337413 transfer_amount: 1112 transferred_account: 4258262637 TL
						//2781337413 hesabından 4258262637 numaralı hesaba 1112 TL transfer edilmiştir.
						String message = accountNumber + " hesabından " + separated[4] + " numaralı hesaba " + separated[2] + " " + separated[5] +" transfer edilmiştir.";
						logList.add(LogResult.builder().log(message).build());
					} else if (separated[1].equals("deposit")) {
						//Example:
						//2781337413 deposit amount: 1112 TL
						//2781337413 numaralı hesaba 1112 TL yatırılmıştır.
						String message = accountNumber + " numaralı hesaba " + separated[3] + " " + separated[4] + " yatırılmıştır.";
						logList.add(LogResult.builder().log(message).build());
					}
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ResponseEntity
				.ok()
				.header("content-type","application/json")
				.body(logList);
	}

}
