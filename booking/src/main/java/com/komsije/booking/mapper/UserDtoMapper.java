package com.komsije.booking.mapper;

import com.komsije.booking.dto.AccountDto;
import com.komsije.booking.dto.GuestDto;
import com.komsije.booking.dto.HostDto;
import com.komsije.booking.dto.UserDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.model.Account;
import com.komsije.booking.model.Guest;
import com.komsije.booking.model.Host;
import com.komsije.booking.model.Role;
import com.komsije.booking.repository.AccountRepository;
import com.komsije.booking.repository.GuestRepository;
import com.komsije.booking.repository.HostRepository;
import com.komsije.booking.service.interfaces.AccountService;
import com.komsije.booking.service.interfaces.GuestService;
import com.komsije.booking.service.interfaces.HostService;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public abstract class UserDtoMapper {
    @Autowired
    private HostRepository hostRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;

    public UserDto toDto(Account account) throws ElementNotFoundException {
        UserDto userDto = new UserDto();
        userDto.setAccountId(account.getId());
        if (account.getRole().equals(Role.Host)){
            Host host = hostRepository.getReferenceById(account.getId());
            userDto.setFirstName(host.getFirstName());
            userDto.setLastName(host.getLastName());
        }else if (account.getRole().equals(com.komsije.booking.model.Role.Guest)){
            Guest guest = guestRepository.getReferenceById(account.getId());
            userDto.setFirstName(guest.getFirstName());
            userDto.setLastName(guest.getLastName());
        }
        return userDto;
    }

    public AccountDto fromDto(UserDto userDto) throws ElementNotFoundException {
        return accountMapper.toDto(accountRepository.getReferenceById(userDto.getAccountId()));
    }
    public Account fromDtoModel(UserDto userDto) throws ElementNotFoundException{
        return accountRepository.getReferenceById(userDto.getAccountId());
    }

}
