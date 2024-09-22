package com.ebanx.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ebanx.challenge.dto.Event;
import com.ebanx.challenge.dto.EventResponse;
import com.ebanx.challenge.exceptions.AccountAlreadyExistsException;
import com.ebanx.challenge.exceptions.AccountNotFoundException;
import com.ebanx.challenge.exceptions.InsufficientBalanceException;
import com.ebanx.challenge.service.AccountService;

@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("/")
    public String redirectToSwagger() {
        return "Challenge EBANX";
    }
	
	@PostMapping("/reset")
	public ResponseEntity<?> reset() {
		accountService.reset();
		return ResponseEntity.status(HttpStatus.OK).body("OK");
	}
	
	@GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBalance(@RequestParam("account_id") String account_id) {
		try {
			float balance = accountService.getBalance(account_id);
			
			return ResponseEntity.ok(balance);
		}catch (AccountNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
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
					
					return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
				}catch (AccountNotFoundException e) {
					eventResponse = accountService.createAccount(event.getDestination(), event.getAmount());
					
					return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
				}
			} case "TRANSFER": {
				eventResponse = accountService.transfer(event.getOrigin(), event.getDestination(), event.getAmount());
				
				return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
			} case "WITHDRAW": {
				eventResponse = accountService.withdraw(event.getOrigin(), event.getAmount());
				
				return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
			} default:
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("0");
			}

		} catch (AccountNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
		} catch (AccountAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("0");
		} catch (InsufficientBalanceException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("0");
		}
	}
	
}
