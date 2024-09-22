package com.ebanx.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ebanx.challenge.dto.Event;
import com.ebanx.challenge.dto.EventResponse;
import com.ebanx.challenge.exceptions.AccountAlreadyExistsException;
import com.ebanx.challenge.exceptions.AccountNotFoundException;
import com.ebanx.challenge.exceptions.InsufficientBalanceException;
import com.ebanx.challenge.service.AccountService;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@PostMapping("/reset")
	public void reset() {
		accountService.reset();
	}
	
	@GetMapping(value = "/balance/{account_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBalance(@PathVariable @Parameter(description = "Id of account") String account_id) {
		try {
			float balance = accountService.getBalance(account_id);
			
			return ResponseEntity.ok(balance);
		}catch (AccountNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> postEvent(@RequestBody Event event, UriComponentsBuilder uriBuilder) {
		try {
			EventResponse eventResponse = null;
			
			switch (event.getType().toUpperCase()) {
			case "DEPOSIT": {
				try {
					eventResponse = accountService.deposit(event.getDestination(), event.getAmount());
					
					return ResponseEntity.ok(eventResponse);
				}catch (AccountNotFoundException e) {
					eventResponse = accountService.createAccount(event.getDestination(), event.getAmount());
					
					return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
				}
			} case "TRANSFER": {
				eventResponse = accountService.transfer(event.getOrigin(), event.getDestination(), event.getAmount());
				
				return ResponseEntity.ok(eventResponse);
			} case "WITHDRAW": {
				eventResponse = accountService.withdraw(event.getOrigin(), event.getAmount());
				
				return ResponseEntity.ok(eventResponse);
			} default:
				return ResponseEntity.badRequest().build();
			}

		} catch (AccountNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (AccountAlreadyExistsException e) {
			return ResponseEntity.badRequest().build();
		} catch (InsufficientBalanceException e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
}
