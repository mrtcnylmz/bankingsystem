package com.mrtcn.bankingSystem.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResult {
	private String message;
}