package com.komsije.booking.service;

import com.komsije.booking.dto.AccountDto;
import com.komsije.booking.dto.LoginDto;
import com.komsije.booking.dto.NewPasswordDto;
import com.komsije.booking.exceptions.AccountNotActivatedException;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.IncorrectPasswordException;
import com.komsije.booking.mapper.AccountMapper;
import com.komsije.booking.model.Account;
import com.komsije.booking.model.Role;
import com.komsije.booking.repository.AccountRepository;
import com.komsije.booking.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    @Autowired
    private AccountMapper mapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDto findById(Long id) throws ElementNotFoundException {
            return mapper.toDto(accountRepository.findById(id).orElseThrow(()->new ElementNotFoundException("Element with given ID doesn't exist!")));
    }

    @Override
    public Account findModelById(Long id) throws ElementNotFoundException {
        return accountRepository.findById(id).orElseThrow(()->new ElementNotFoundException("Element with given ID doesn't exist!"));
    }

    public List<AccountDto> findAll() {
        return mapper.toDto(accountRepository.findAll());
    }

    public AccountDto save(AccountDto accountDto) {
        accountRepository.save(mapper.fromDto(accountDto));
        return accountDto;
    }

    @Override
    public AccountDto update(AccountDto accountDto) throws  ElementNotFoundException {
        Account account = accountRepository.findById(accountDto.getId()).orElseThrow(()->new ElementNotFoundException("Element with given ID doesn't exist!"));
        mapper.update(account, accountDto);
        accountRepository.save(account);
        return accountDto;
    }

    public void delete(Long id) throws NoSuchElementException{
        Account account = accountRepository.findById(id).orElseThrow();
        accountRepository.deleteById(id); // warning: deleting from account service doesn't check for active reservations
    }

    public List<AccountDto> getByAccountType(Role type) {
        return mapper.toDto(accountRepository.findAccountByRole(type));
    }

    public List<AccountDto> getBlockedAccounts() {
        return mapper.toDto(accountRepository.findAccountByIsBlocked(true));
    }

    @Override
    public AccountDto getByEmail(String email) throws ElementNotFoundException {
        Account account = accountRepository.getAccountByEmail(email);
        if (account == null){
            throw new ElementNotFoundException("Account with given email doesn't exit!");
        }
        return mapper.toDto(account);
    }
    @Override
    public Account getModelByEmail(String email) throws ElementNotFoundException {
        Account account = accountRepository.getAccountByEmail(email);
        if (account == null){
            throw new ElementNotFoundException("Account with given email doesn't exit!");
        }
        return account;
    }
    @Override
    public String getEmail(Long id){
        Account account = accountRepository.findById(id).orElseThrow();
        return account.getEmail();
    }

    @Override
    public AccountDto checkLoginCredentials(LoginDto loginDto) throws ElementNotFoundException, AccountNotActivatedException, IncorrectPasswordException {
        Account account = accountRepository.getAccountByEmail(loginDto.getEmail());
        if (account == null){
            throw new ElementNotFoundException("Account with given email doesn't exist!");
        }
        if (!account.isActivated()){
            throw new AccountNotActivatedException("Account exists but it is not activated!");
        }
        if (account.getPassword().equals(loginDto.getPassword())){
            return mapper.toDto(account);
        }else{
            throw new IncorrectPasswordException("Given password is not valid!");
        }

    }

    @Override
    public void activateAccount(String email) {
        Account account = accountRepository.getAccountByEmail(email);
        account.setActivated(true);
        accountRepository.save(account);
    }

    @Override
    public void changePassword(NewPasswordDto newPasswordDto) throws ElementNotFoundException, IncorrectPasswordException {
        Account account = accountRepository.getAccountByEmail(newPasswordDto.getEmail());
        if (account==null)
            throw new ElementNotFoundException("Account with given email doesn't exist!");
        if (!Objects.equals(account.getPassword(), newPasswordDto.getOldPassword()))
            throw new IncorrectPasswordException("Old password is incorrect");
        account.setPassword(newPasswordDto.getNewPassword());
        accountRepository.save(account);


    }


}
