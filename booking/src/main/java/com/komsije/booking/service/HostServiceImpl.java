package com.komsije.booking.service;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.HostDto;
import com.komsije.booking.dto.HostPropertyDto;
import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.EmailAlreadyExistsException;
import com.komsije.booking.exceptions.HasActiveReservationsException;
import com.komsije.booking.mapper.AddressMapper;
import com.komsije.booking.mapper.HostMapper;
import com.komsije.booking.model.Accommodation;
import com.komsije.booking.model.Account;
import com.komsije.booking.model.ConfirmationToken;
import com.komsije.booking.model.Host;
import com.komsije.booking.repository.AccountRepository;
import com.komsije.booking.repository.HostRepository;
import com.komsije.booking.service.interfaces.AccommodationService;
import com.komsije.booking.service.interfaces.ConfirmationTokenService;
import com.komsije.booking.service.interfaces.HostService;
import com.komsije.booking.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class HostServiceImpl implements HostService {
    @Autowired
    private HostMapper mapper;
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final HostRepository hostRepository;
    private final AccountRepository accountRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final ReservationService reservationService;
    private final TaskScheduler taskScheduler;
    private final AccommodationService accommodationService;


    @Autowired
    public HostServiceImpl(HostRepository hostRepository, AccountRepository accountRepository, ConfirmationTokenService confirmationTokenService, ReservationService reservationService, TaskScheduler taskScheduler, AccommodationService accommodationService) {
        this.hostRepository = hostRepository;
        this.accountRepository = accountRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.reservationService = reservationService;
        this.taskScheduler = taskScheduler;
        this.accommodationService = accommodationService;
    }

    public HostDto findById(Long id) throws ElementNotFoundException {
        return mapper.toDto(hostRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!")));
    }

    public List<HostDto> findAll() {
        return mapper.toDto(hostRepository.findAll());
    }

    public HostDto save(HostDto hostDto) {
        Host host = hostRepository.save(mapper.fromDto(hostDto));
        return mapper.toDto(host);
    }

    @Override
    public HostDto update(HostDto hostDto) throws ElementNotFoundException {
        Host host = hostRepository.findById(hostDto.getId()).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        mapper.update(host, hostDto);
        Host savedHost = hostRepository.save(host);
        return mapper.toDto(savedHost);
    }

    private void deleteHostAccommodations(Long hostId){
        List<HostPropertyDto> accommodations = this.accommodationService.findByHostId(hostId);
        for (int i =0; i< accommodations.size(); i++){
            this.accommodationService.delete(accommodations.get(i).getId());
        }
    }



    public void delete(Long id) throws ElementNotFoundException {
        if (hostRepository.existsById(id)) {
            if (!reservationService.hasHostActiveReservations(id)) {
                deleteHostAccommodations(id);
                hostRepository.deleteById(id);
            } else {
                throw new HasActiveReservationsException("Account has active reservations and can't be deleted!");
            }
        } else {
            throw new ElementNotFoundException("Element with given ID doesn't exist!");
        }

    }


    @Override
    public Long singUpUser(RegistrationDto registrationDto) {
        Account account = accountRepository.findById(registrationDto.getId()).orElse(null);
        Long id;
        if (account == null) {
            Host host = mapper.fromRegistrationDto(registrationDto);
            hostRepository.save(host);
            id = host.getId();
        } else {
            throw new EmailAlreadyExistsException("Email already exists!");

        }
        return id;
    }


}
