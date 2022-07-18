package com.mrtcn.bankingSystem.Interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.TransferResult;

public interface ITransferService {
	public ResponseEntity<TransferResult> transfer(String accountNumber, TransferRequest request, KafkaTemplate<String,String> producer);
}
