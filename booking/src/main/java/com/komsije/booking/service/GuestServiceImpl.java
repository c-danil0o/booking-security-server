package com.komsije.booking.service;

import com.komsije.booking.dto.*;
import com.komsije.booking.exceptions.*;
import com.komsije.booking.mapper.AccommodationMapper;
import com.komsije.booking.mapper.AddressMapper;
import com.komsije.booking.mapper.GuestMapper;
import com.komsije.booking.model.*;
import com.komsije.booking.repository.AccountRepository;
import com.komsije.booking.repository.GuestRepository;
import com.komsije.booking.service.interfaces.ConfirmationTokenService;
import com.komsije.booking.service.interfaces.GuestService;
import com.komsije.booking.service.interfaces.NotificationService;
import com.komsije.booking.service.interfaces.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GuestServiceImpl implements GuestService {
    @Autowired
    private GuestMapper mapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private AccommodationMapper accommodationMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final GuestRepository guestRepository;
    private final AccountRepository accountRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final ReservationService reservationService;
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;

    @Autowired
    public GuestServiceImpl(GuestRepository guestRepository, AccountRepository accountRepository, ConfirmationTokenService confirmationTokenService, ReservationService reservationService, TaskScheduler taskScheduler, NotificationService notificationService) {
        this.guestRepository = guestRepository;
        this.accountRepository = accountRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.reservationService = reservationService;
        this.taskScheduler = taskScheduler;
        this.notificationService = notificationService;
    }

    public GuestDto findById(Long id) throws ElementNotFoundException {
        return mapper.toDto(guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!")));
    }

    public List<GuestDto> findAll() {
        return mapper.toDto(guestRepository.findAll());
    }

    public GuestDto save(GuestDto guestDto) {
        guestRepository.save(mapper.fromDto(guestDto));
        return guestDto;
    }

    @Override
    public GuestDto update(GuestDto guestDto) throws ElementNotFoundException {
        Guest guest = guestRepository.findById(guestDto.getId()).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        mapper.update(guest, guestDto);
        Guest savedGuest = guestRepository.save(guest);
        return mapper.toDto(savedGuest);
    }

    public void deleteGuestReservations(Long id) {
        if (guestRepository.existsById(id)) {
            reservationService.deleteInBatch(this.reservationService.getByGuestId(id).stream().map(ReservationViewDto::getId).toList());
        }
    }

    @Override
    public void delete(Long id) throws ElementNotFoundException {
        if (guestRepository.existsById(id)) {
            if (!reservationService.hasActiveReservations(id)) {
                deleteGuestReservations(id);
                guestRepository.deleteById(id);

            } else {
                throw new HasActiveReservationsException("Account has active reservations and can't be deleted!");
            }
        } else {
            throw new ElementNotFoundException("Element with given ID doesn't exist!");
        }

    }

    @Override
    public List<AccommodationDto> getFavoritesByGuestId(Long id) throws ElementNotFoundException {
        Guest guest = guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        return accommodationMapper.toDto(guest.getFavorites().stream().toList());
    }

    @Override
    public List<AccommodationDto> addToFavorites(Long id, AccommodationDto accommodationDto) throws ElementNotFoundException {
        Guest guest = guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        guest.getFavorites().add(accommodationMapper.fromDto(accommodationDto));
        guestRepository.save(guest);
        return accommodationMapper.toDto(guest.getFavorites().stream().toList());
    }

    @Override
    public Long singUpUser(RegistrationDto registrationDto) {
        Account account = accountRepository.findById(registrationDto.getId()).orElse(null);
        Long id;
        if (account == null) {
            Guest guest = mapper.fromRegistrationDto(registrationDto);
            guestRepository.save(guest);
            id = guest.getId();
        } else {
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        return id;
    }


    @Override
    public void increaseCancelations(Long id) throws ElementNotFoundException {
        Guest guest = guestRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        int timesCancelled = guest.getTimesCancelled();
        guest.setTimesCancelled(timesCancelled + 1);
        guestRepository.save(guest);
    }

    @Override
    public boolean cancelReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException, CancellationDeadlineExpiredException {
        ReservationDto reservation = reservationService.findById(id);
        if (reservation.getReservationStatus().equals(ReservationStatus.Approved)){
            if (reservation.getDateCreated().plusDays(reservationService.getCancellationDeadline(id)).isBefore(LocalDate.now())){
                throw new CancellationDeadlineExpiredException("Cancellation deadline is expired!");
            } else {
                reservation.setReservationStatus(ReservationStatus.Cancelled);
                reservationService.updateStatus(reservation.getId(), ReservationStatus.Cancelled);
                reservationService.restoreTimeslots(reservation.getId());
                this.increaseCancelations(reservation.getGuestId());
            }
        } else if (reservation.getReservationStatus().equals(ReservationStatus.Pending)) {
            reservation.setReservationStatus(ReservationStatus.Cancelled);
            reservationService.updateStatus(reservation.getId(), ReservationStatus.Cancelled);
        } else {
            throw new PendingReservationException("Reservation is not in pending or approved state!");
        }
        Guest guest = guestRepository.findById(reservation.getGuestId()).orElse(null);
        Account host = accountRepository.findById(reservation.getHostId()).orElse(null);
        if (host.getSettings().contains(Settings.RESERVATION_CANCEL_NOTIFICATION)) {
            StringBuilder mess = new StringBuilder();
            mess.append("Guest ").append(guest.getFirstName()).append(" ").append(guest.getLastName()).append(" has cancelled reservation request for your accommodation!");
            Notification notification = new Notification(null, mess.toString(), LocalDateTime.now(), accountRepository.findById(reservation.getHostId()).orElse(null));
            notificationService.saveAndSendNotification(notification);
        }

        return true;
    }

    @Override
    public void addFavorite(Long guestId, Long accommodationId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        Set<Accommodation> favorites = guest.getFavorites();
        for (Accommodation acc : favorites) {
            if (acc.getId().equals(accommodationId)) {
                throw new FavoriteAlreadyExistsException("This accommodation is already in this guests favorites!");
            }
        }
        AccommodationDto dto = new AccommodationDto();
        dto.setId(accommodationId);
        favorites.add(accommodationMapper.fromDto(dto));
        guest.setFavorites(favorites);
        guestRepository.save(guest);
    }

    @Override
    public void removeFavorite(Long guestId, Long accommodationId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        Accommodation forRemoval = null;
        Set<Accommodation> favorites = guest.getFavorites();

        for (Accommodation acc : favorites) {
            if (acc.getId().equals(accommodationId)) {
                forRemoval = acc;
            }
        }
        if (forRemoval == null) {
            throw new ElementNotFoundException("Favorite accommodation not found!");
        }
        favorites.remove(forRemoval);
        guest.setFavorites(favorites);
        guestRepository.save(guest);
    }

    @Override
    public boolean checkIfInFavorites(Long guestId, Long accommodationId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ElementNotFoundException("Element with given ID doesn't exist!"));
        for (Accommodation acc : guest.getFavorites()) {
            if (acc.getId().equals(accommodationId)) {
                return true;
            }
        }
        return false;
    }
}
