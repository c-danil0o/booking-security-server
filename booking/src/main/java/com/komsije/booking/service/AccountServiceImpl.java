package com.komsije.booking.service;

import com.komsije.booking.dto.AccountDto;
import com.komsije.booking.dto.LoginDto;
import com.komsije.booking.dto.NewPasswordDto;
import com.komsije.booking.exceptions.AccountNotActivatedException;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.IncorrectPasswordException;
import com.komsije.booking.mapper.AccountMapper;
import com.komsije.booking.model.*;
import com.komsije.booking.repository.AccountRepository;
import com.komsije.booking.repository.ReportRepository;
import com.komsije.booking.repository.ReservationRepository;
import com.komsije.booking.service.interfaces.AccommodationService;
import com.komsije.booking.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final ReportRepository reportRepository;
    private final ReservationRepository reservationRepository;
    private final AccommodationService accommodationService;

    @Autowired
    private AccountMapper mapper;


    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ReportRepository reportRepository, ReservationRepository reservationRepository, AccommodationService accommodationService) {
        this.accountRepository = accountRepository;
        this.reportRepository = reportRepository;
        this.reservationRepository = reservationRepository;
        this.accommodationService = accommodationService;
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






    private void deleteReports(Long userId){
        List<Report> reports = reportRepository.findAll();
        List<Report> forRemoval = new ArrayList<>();
        for (Report r: reports) {
            if(r.getReportedUser().getId()==userId)
                forRemoval.add(r);
        }
        reportRepository.deleteAll(forRemoval);
    }

    private void deleteReservations(Long userId){
        List<Reservation> reservations = reservationRepository.findByGuestId(userId);
        for (Reservation r : reservations) {
            ReservationStatus status = r.getReservationStatus();
            if (status.equals(ReservationStatus.Pending) || status.equals(ReservationStatus.Approved)){
                r.setReservationStatus(ReservationStatus.Denied);
                reservationRepository.save(r);
                accommodationService.restoreTimeslot(r);
            }
        }
        //NE PISE U SPEC ALI IMA SMISLA - BRISANJE HOST REZERVACIJA
//        reservations = reservationRepository.findByHostId(userId);
//        for (Reservation r : reservations) {
//            ReservationStatus status = r.getReservationStatus();
//            if (status.equals(ReservationStatus.Pending) || status.equals(ReservationStatus.Approved)){
//                r.setReservationStatus(ReservationStatus.Denied);
//                reservationRepository.save(r);
//            }
//        }

    }

    @Override
    public void applySettings(Long userId, List<String> settings) {
        Account account = accountRepository.findById(userId).orElseThrow(()->new ElementNotFoundException("Element with given ID doesn't exist!"));
        Set<Settings> newSettings = new HashSet<>();
        for (String setting: settings){
            newSettings.add(Settings.valueOf(setting));
        }
        account.setSettings(newSettings);
        accountRepository.save(account);
    }


}
