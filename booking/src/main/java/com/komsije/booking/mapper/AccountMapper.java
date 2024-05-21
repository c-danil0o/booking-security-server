package com.komsije.booking.mapper;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.AccountDto;
import com.komsije.booking.model.Accommodation;
import com.komsije.booking.model.Account;
import com.komsije.booking.repository.AccountRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public abstract class AccountMapper {
    @Autowired
    private AccountRepository accountRepository;
    public abstract  AccountDto toDto(Account account);
    public Account fromDto(AccountDto accountDto){
        if ( accountDto == null ) {
            return null;
        }
        if (accountDto.getId() != null && accountRepository.existsById(accountDto.getId())){
            return accountRepository.findById(accountDto.getId()).orElse(null);
        }

        Account account = new Account();

        account.setId( accountDto.getId() );
        account.setRole( accountDto.getRole() );

        return account;
    }
    public abstract  List<AccountDto> toDto(List<Account> accountList);
    public abstract  void update(@MappingTarget Account account, AccountDto accountDto);

}
