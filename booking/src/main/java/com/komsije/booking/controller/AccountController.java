package com.komsije.booking.controller;

import com.komsije.booking.dto.*;
import com.komsije.booking.exceptions.AccountBlockedException;
import com.komsije.booking.exceptions.AccountNotActivatedException;
import com.komsije.booking.model.Role;
import com.komsije.booking.service.RegistrationServiceImpl;
import com.komsije.booking.service.interfaces.AccountService;
import com.komsije.booking.validators.IdentityConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api")
@Validated

public class AccountController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RegistrationServiceImpl registrationService;
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/accounts/all")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.findAll();
        return new ResponseEntity<>(accounts, HttpStatus.OK);

    }


    //@PreAuthorize("hasRole('Admin')")

    @GetMapping(value = "/accounts/{id}")
    public ResponseEntity<AccountDto> getAccount(@IdentityConstraint  @PathVariable Long id) {
        AccountDto account = accountService.findById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/accounts")
    public ResponseEntity<List<AccountDto>> getAccountsByType(@RequestParam String type) {
        List<AccountDto> accounts = accountService.getByAccountType(Role.valueOf(type));
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PostMapping(value = "/accounts/email")
    public ResponseEntity<AccountDto> getAccountByEmail(@Valid  @RequestBody EmailDto emailDto) {

        AccountDto account = accountService.getByEmail(emailDto.getEmail());
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/accounts/blocked")
    public ResponseEntity<List<AccountDto>> getBlockedAccounts() {

        List<AccountDto> accounts = accountService.getBlockedAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

/*    @PostMapping(value = "/accounts/save", consumes = "application/json")
    public ResponseEntity<AccountDto> saveAccount(@RequestBody AccountDto accountDTO) {
        AccountDto account = accountService.save(accountDTO);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }*/

    @PreAuthorize("hasRole('Admin')")
    @PatchMapping(value = "/accounts/{id}/block")
    public ResponseEntity<Void> blockAccount(@IdentityConstraint @PathVariable("id") Long id) {
        this.accountService.blockAccount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@IdentityConstraint @PathVariable Long id) {

        accountService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        if (!this.accountService.getByEmail(loginDto.getEmail()).isActivated()){
            throw new AccountNotActivatedException("Account is not activated!");
        }
        if (this.accountService.getByEmail(loginDto.getEmail()).isBlocked()){
            throw new AccountBlockedException("Your account is blocked!");
        }
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDto.getEmail(),
                loginDto.getPassword());
        Authentication auth = authenticationManager.authenticate(authReq);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        UserDetails userDetail = userDetailsService.loadUserByUsername(loginDto.getEmail());

        String token = "";
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }


    @GetMapping(
            value = "/logout",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> logoutUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //if (!(auth instanceof AnonymousAuthenticationToken)){
        if (true) {
            SecurityContextHolder.clearContext();

            return new ResponseEntity<>("You successfully logged out!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping(value = "/accounts/update", consumes = "application/json")
    public ResponseEntity<AccountDto> updateAccount(@Valid @RequestBody AccountDto accountDto) {
        AccountDto account = accountService.update(accountDto);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<TokenDto> register1(@RequestHeader(HttpHeaders.USER_AGENT) String agent, @Valid @RequestBody RegistrationDto registrationDto) {

        if (agent.equals("Mobile-Android")) {
            return new ResponseEntity<>(registrationService.registerAndroid(registrationDto), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(registrationService.register(registrationDto), HttpStatus.OK);
        }

    }

    @GetMapping(path = "/register/confirm")
    public ResponseEntity<String> confirm(@NotNull  @RequestParam("token") String token) {
        return new ResponseEntity<>(registrationService.confirmToken(token), HttpStatus.OK);
    }

    @PostMapping(value = "/passwordChange", consumes = "application/json")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody NewPasswordDto newPasswordDto) {
        accountService.changePassword(newPasswordDto);
        return new ResponseEntity<>(HttpStatus.OK);

    }


}
