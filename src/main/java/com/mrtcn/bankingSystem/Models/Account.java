package com.mrtcn.bankingSystem.Models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long number;
	private int balance;
    private String name;
    private String surname;
    private String email;
    private String tc;
    private String type;
    private Instant lastUpdate;

    public String toFileFormat(){
        return "Account Number: " + this.number + "\n" + 
        		"Account Tc Number: " + this.tc + "\n" +
        		"Account Owner: " + this.name + " " + this.surname + "\n" +
        		"Account Email: " + this.email + "\n" +
        		"Account Balance: " + this.balance + " " + this.type;
    }
}
