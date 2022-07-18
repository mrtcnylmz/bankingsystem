package com.mrtcn.bankingSystem.Interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.mrtcn.bankingSystem.Responses.LogResult;

public interface ILogService {
	public ResponseEntity<List<LogResult>> getLog(String accountNumber);
}
