package com.komsije.booking.service;

import com.komsije.booking.model.Account;
import com.komsije.booking.model.AccountType;
import com.komsije.booking.repository.AccountRepository;
import com.komsije.booking.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findById(Long id) {return accountRepository.findById(id).orElseGet(null);}
    public List<Account> findAll() {return accountRepository.findAll();}
    public Account save(Account accommodation) {return accountRepository.save(accommodation);}
    public void delete(Long id) {
        accountRepository.deleteById(id);}
    public List<Account> getByAccountType(AccountType type){
        return accountRepository.findAccountByAccountType(type);
    }

    public List<Account> getBlockedAccounts() {return accountRepository.findAccountByIsBlocked(true);}

    @Override
    public Account getByEmail(String email) {
        return accountRepository.getAccountByEmail(email);
    }
}
