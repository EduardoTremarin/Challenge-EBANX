package com.ebanx.challenge.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ebanx.challenge.dto.AccountDTO;
import com.ebanx.challenge.dto.EventResponse;
import com.ebanx.challenge.exceptions.AccountAlreadyExistsException;
import com.ebanx.challenge.exceptions.AccountNotFoundException;
import com.ebanx.challenge.exceptions.InsufficientBalanceException;
import com.ebanx.challenge.model.Account;

@Service
public class AccountService {

	private List<Account> accounts = new ArrayList<>();
	
	/**
	 * Clear accounts list.
	 */
	public void reset() {
		this.accounts = new ArrayList<>();
	}
	
	/**
	 * Return the balance from account found by id in accounts list.
	 * Throws an exception if the account is not found.
	 * @param account_id
	 * @return
	 */
	public float getBalance(String account_id) {
		Account account = findAccount(account_id);
		
		if(account == null)
			throw new AccountNotFoundException("Account not found.");
		
		return account.getBalance();
	}
	
	/**
	 * Create account by destination and initial balance.
	 * Throws an exception if the account already exists.
	 * @param destination
	 * @param amount
	 * @return
	 */
	public EventResponse createAccount(String destination, float amount) {
		if(findAccount(destination) != null)
			throw new AccountAlreadyExistsException("An account with this ID already exists.");
		
		Account account = new Account(destination, amount);
		
		this.accounts.add(account);
		
		EventResponse eventResponse = new EventResponse();
		AccountDTO accountDTO = new AccountDTO();
		
		accountDTO.setId(account.getId());
		accountDTO.setBalance(account.getBalance());
		
		eventResponse.setDestination(accountDTO);
		
		return eventResponse;
	}
	
	/**
	 * Deposit a specified amount in account.
	 * Throws an exception if the account is not found.
	 * @param destination
	 * @param amount
	 * @return
	 */
	public EventResponse deposit(String destination, float amount) {
		Account account = findAccount(destination);
		
		if(account == null)
			throw new AccountNotFoundException("Account not found.");
		
		account.setBalance(account.getBalance() + amount);
		
		EventResponse eventResponse = new EventResponse();
		AccountDTO accountDTO = new AccountDTO();
		
		accountDTO.setId(account.getId());
		accountDTO.setBalance(account.getBalance());
		
		eventResponse.setDestination(accountDTO);
		
		return eventResponse;
	}
	
	/**
	 * Transfer a specified amount from origin to destination.
	 * Throws an exception if some account is not found or if the balance from origin is insufficient.
	 * @param origin
	 * @param destination
	 * @param amount
	 * @return
	 */
	public EventResponse transfer(String origin, String destination, float amount) {
		Account accountDestination = findAccount(destination);
		Account accountOrigin = findAccount(origin);
		
		if(accountOrigin == null)
			throw new AccountNotFoundException("Origin not found.");
		
		if(accountDestination == null) {
			String id = this.createAccount(destination, 0).getDestination().getId();
			accountDestination = findAccount(id);
		}
		
		if(accountOrigin.getBalance() < amount)
			throw new InsufficientBalanceException("Insufficient Balance.");
		
		accountDestination.setBalance(accountDestination.getBalance() + amount);
		accountOrigin.setBalance(accountOrigin.getBalance() - amount);
		
		EventResponse eventResponse = new EventResponse();
		
		AccountDTO accountOriginDTO = new AccountDTO();
		accountOriginDTO.setId(accountOrigin.getId());
		accountOriginDTO.setBalance(accountOrigin.getBalance());
		
		eventResponse.setOrigin(accountOriginDTO);
		
		AccountDTO accountDestinationDTO = new AccountDTO();
		accountDestinationDTO.setId(accountDestination.getId());
		accountDestinationDTO.setBalance(accountDestination.getBalance());
		
		eventResponse.setDestination(accountDestinationDTO);
		
		return eventResponse;
	}
	
	/**
	 * Withdraws a specified amount from the account. 
	 * Throws an exception if the account is not found or if the balance is insufficient.
	 * @param origin
	 * @param amount
	 * @return
	 */
	public EventResponse withdraw(String origin, float amount) {
		Account account = findAccount(origin);
		
		if(account == null)
			throw new AccountNotFoundException("Account not found.");
		
		if(account.getBalance() < amount)
			throw new InsufficientBalanceException("Insufficient Balance.");
		
		account.setBalance(account.getBalance() - amount);
		
		EventResponse eventResponse = new EventResponse();
		AccountDTO accountDTO = new AccountDTO();
		
		accountDTO.setId(account.getId());
		accountDTO.setBalance(account.getBalance());
		
		eventResponse.setOrigin(accountDTO);
		
		return eventResponse;
	}
	
	/**
	 * Search account by id, in accounts list.
	 * @param account_id
	 * @return
	 */
	private Account findAccount(String account_id) {
		if(accounts == null || accounts.isEmpty())
			return null;
		
		Optional<Account> optionalAccount = accounts.stream()
				.filter(account -> account.getId().contentEquals(account_id))
				.findFirst();
		
		if(optionalAccount.isEmpty())
			return null;
		
		return optionalAccount.get();
	}
	
}
