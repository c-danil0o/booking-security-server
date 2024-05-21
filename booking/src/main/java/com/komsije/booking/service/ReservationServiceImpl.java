package com.komsije.booking.service;

import com.komsije.booking.dto.ReservationDto;
import com.komsije.booking.dto.ReservationViewDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.InvalidTimeSlotException;
import com.komsije.booking.exceptions.PendingReservationException;
import com.komsije.booking.exceptions.ReservationAlreadyExistsException;
import com.komsije.booking.mapper.ReservationMapper;
import com.komsije.booking.model.*;
import com.komsije.booking.repository.ReservationRepository;
import com.komsije.booking.service.interfaces.AccommodationService;
import com.komsije.booking.service.interfaces.AccountService;
import com.komsije.booking.service.interfaces.NotificationService;
import com.komsije.booking.service.interfaces.ReservationService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationMapper mapper;
    private final TaskScheduler taskScheduler;
    private final ReservationRepository reservationRepository;
    private final AccommodationService accommodationService;
    private final NotificationService notificationService;
    private final AccountService accountService;
    private static final Logger LOG = Logger.getAnonymousLogger();

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, AccommodationService accommodationService, ReservationMapper mapper, TaskScheduler taskScheduler, NotificationService notificationService, AccountService accountService) {
        this.reservationRepository = reservationRepository;
        this.accommodationService = accommodationService;
        this.mapper = mapper;
        this.taskScheduler = taskScheduler;
        this.notificationService = notificationService;
        this.accountService = accountService;
    }

    public ReservationDto findById(Long id) throws ElementNotFoundException {
        return mapper.toDto(reservationRepository.findById(id).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!")));
    }

    public List<ReservationDto> findAll() {
        return mapper.toDto(reservationRepository.findAll());
    }

    public List<ReservationViewDto> getAll(){
        return mapper.toViewDto(reservationRepository.findAll());
    }


    @Override
    public List<ReservationViewDto> getByHostId(Long id) {
        List<Reservation> reservations = this.reservationRepository.findByHostId(id);
        return mapper.toViewDto(reservations);
    }

    @Override
    public List<ReservationViewDto> getByGuestId(Long id) {
        List<Reservation> reservations = this.reservationRepository.findByGuestId(id);
        return mapper.toViewDto(reservations);
    }

    @Override
    public List<ReservationViewDto> getRequestsByHostId(Long id) {
        List<ReservationViewDto> reservations = getByHostId(id);
        List<ReservationViewDto> requests = new ArrayList<>();
        for (ReservationViewDto reservation: reservations) {
            ReservationStatus status = reservation.getReservationStatus();
            if (status.equals(ReservationStatus.Pending))
                requests.add(reservation);
        }
        return requests;
    }

    @Override
    public List<ReservationViewDto> getRequestsByGuestId(Long id) {
        List<ReservationViewDto> reservations = getByGuestId(id);
        List<ReservationViewDto> requests = new ArrayList<>();
        for (ReservationViewDto reservation: reservations) {
            ReservationStatus status = reservation.getReservationStatus();
            if (status.equals(ReservationStatus.Pending) || status.equals(ReservationStatus.Approved))
                requests.add(reservation);
        }
        return requests;
    }

    @Override
    public List<ReservationViewDto> getDecidedByHostId(Long id) {
        List<ReservationViewDto> reservations = getByHostId(id);
        List<ReservationViewDto> requests = new ArrayList<>();
        for (ReservationViewDto reservation: reservations) {
            ReservationStatus status = reservation.getReservationStatus();
            if (!(status.equals(ReservationStatus.Pending) || status.equals(ReservationStatus.Cancelled) || status.equals(ReservationStatus.Denied)))
                requests.add(reservation);
        }
        return requests;
    }

    @Override
    public List<ReservationViewDto> getDecidedByGuestId(Long id) {
        List<ReservationViewDto> reservations = getByGuestId(id);
        List<ReservationViewDto> requests = new ArrayList<>();
        for (ReservationViewDto reservation: reservations) {
            ReservationStatus status = reservation.getReservationStatus();
            if (!status.equals(ReservationStatus.Pending))
                requests.add(reservation);
        }
        return requests;
    }

    public List<ReservationDto> getByReservationStatus(ReservationStatus reservationStatus){return mapper.toDto(reservationRepository.findReservationsByReservationStatus(reservationStatus));}

    @Override
    public boolean hasActiveReservations(Long accountId) {
        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation: reservations){
            if (reservation.getGuestId().equals(accountId) && reservation.getReservationStatus().equals(ReservationStatus.Active)){
                return true;
            }
        }
        return false;
    }
    @Override
    public Integer getCancellationDeadline(Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        return reservation.getAccommodation().getCancellationDeadline();
    }
    @Override
    public boolean hasHostActiveReservations(Long accountId) {
        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation: reservations){
            if (reservation.getHostId().equals(accountId) && reservation.getReservationStatus().equals(ReservationStatus.Active)){
                return true;
            }
        }
        return false;
    }
    @Override
    public void restoreTimeslots(Long reservationId) throws ElementNotFoundException{
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        accommodationService.restoreTimeslot(reservation);
    }
    @Override
    public boolean overlappingActiveReservationsExist(LocalDate startDate, LocalDate endDate) throws InvalidTimeSlotException {
        if (startDate.isAfter(endDate)){
            throw new InvalidTimeSlotException("Start date is after end date");
        }
       List<Reservation> reservations = reservationRepository.findReservationsByReservationStatus(ReservationStatus.Active);
       for(Reservation reservation: reservations){
           if (startDate.isBefore(reservation.getStartDate().plusDays(reservation.getDays()))&& reservation.getStartDate().isBefore(endDate)){
               return true;
           }
       }
        return false;
    }

    @Override
    public boolean deleteRequest(Long id) throws ElementNotFoundException, PendingReservationException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        if (reservation.getReservationStatus().equals(ReservationStatus.Pending)){
            reservationRepository.delete(reservation);
            return true;
        }else{
            throw new PendingReservationException("Can't delete non pending reservations!");
        }

    }

    @Override
    public ReservationDto updateStatus(Long id, ReservationStatus status) throws ElementNotFoundException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        reservation.setReservationStatus(status);
        return mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    public boolean acceptReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        if(reservation.getReservationStatus().equals(ReservationStatus.Pending)){
            reservation.setReservationStatus(ReservationStatus.Approved);
            accommodationService.reserveTimeslot(reservation.getAccommodation().getId(),reservation.getStartDate(), reservation.getStartDate().plusDays(reservation.getDays()));
            reservationRepository.save(reservation);
            Runnable task1 = () -> setStatusToActive(reservation);
            Runnable task2 = () -> setStatusToDone(reservation);
            Instant endDate = reservation.getStartDate().plusDays(reservation.getDays()).atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant startDate = reservation.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC);
            LOG.log(Level.INFO, "Scheduled task to set reservation "+ reservation.getId() + " to active on " + startDate);
            LOG.log(Level.INFO, "Scheduled task to set reservation "+ reservation.getId() + " to done on " + endDate);
            taskScheduler.schedule(task1, startDate);
            taskScheduler.schedule(task2, endDate);
            denyOverlappingRequests(reservation.getStartDate(), reservation.getStartDate().plusDays(reservation.getDays()), reservation.getAccommodation().getId());
            sendRequestApprovedNotification(reservation);
        }else{
            throw new PendingReservationException("Reservation is not in pending state!");
        }
        return true;
    }
    private void sendRequestApprovedNotification(Reservation reservation){
        Account guest = accountService.findModelById(reservation.getGuestId());
        if (guest.getSettings().contains(Settings.RESERVATION_RESPONSE_NOTIFICATION)){
            StringBuilder mess = new StringBuilder();
            mess.append("Host ").append(accountService.findModelById(reservation.getHostId()).getId()).append(" has approved your reservation request!");
            Notification notification = new Notification(null, mess.toString(), LocalDateTime.now(),guest);
            notificationService.saveAndSendNotification(notification);
        }
    }

    public void denyOverlappingRequests(LocalDate startDate, LocalDate endDate, Long accommodationId){
        List<Reservation> reservations = reservationRepository.findPendingByAccommodationId(accommodationId);
        for(Reservation res: reservations){
            LocalDate resStartDate = res.getStartDate();
            LocalDate resEndDate = res.getStartDate().plusDays(res.getDays());
            if (startDate.isBefore(resEndDate) && resStartDate.isBefore(endDate)){
                res.setReservationStatus(ReservationStatus.Denied);
                reservationRepository.save(res);
            }
        }

    }

    @SneakyThrows
    private void setStatusToActive(Reservation reservation){

        if(reservation.getReservationStatus().equals(ReservationStatus.Approved)){
            if (reservation.getStartDate().plusDays(reservation.getDays()).isBefore(LocalDate.now())){
                LOG.log(Level.INFO, "Setting status to DONE for reservation:"+ reservation.getId());
                reservation.setReservationStatus(ReservationStatus.Done);
                reservationRepository.save(reservation);
                return;
            }
            LOG.log(Level.INFO, "Setting status to ACTIVE for reservation:"+ reservation.getId());
            reservation.setReservationStatus(ReservationStatus.Active);
            reservation = reservationRepository.save(reservation);

        }
    }
    @SneakyThrows
    private void setStatusToDone(Reservation reservation){
        if (reservation.getReservationStatus().equals(ReservationStatus.Active)){
            LOG.log(Level.INFO, "Setting status to DONE for reservation:"+ reservation.getId());
            reservation.setReservationStatus(ReservationStatus.Done);
            reservation = reservationRepository.save(reservation);
        }
    }
    private void checkIfNotApproved(Reservation reservation){
        if (reservation.getReservationStatus().equals(ReservationStatus.Pending)){
            LOG.log(Level.INFO, "Setting status to DENIED for reservation:"+ reservation.getId());
            reservation.setReservationStatus(ReservationStatus.Denied);
            LOG.log(Level.INFO, reservation.getReservationStatus().toString());

            reservation = reservationRepository.save(reservation);
        }
    }



    @Override
    public boolean denyReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        ReservationStatus status = reservation.getReservationStatus();
        if(status.equals(ReservationStatus.Pending)){
            reservation.setReservationStatus(ReservationStatus.Denied);
            reservationRepository.save(reservation);
            sendRequestDeniedNotification(reservation);
        }else{
            throw new PendingReservationException("Reservation is not in pending state!");
        }
        return true;
    }

    private void sendRequestDeniedNotification(Reservation reservation){
        Account guest = accountService.findModelById(reservation.getGuestId());
        if (guest.getSettings().contains(Settings.RESERVATION_RESPONSE_NOTIFICATION)){
            StringBuilder mess = new StringBuilder();
            mess.append("Host ").append(accountService.findModelById(reservation.getHostId()).getId()).append(" has denied your reservation request!");
            Notification notification = new Notification(null, mess.toString(), LocalDateTime.now(),guest);
            notificationService.saveAndSendNotification(notification);
        }
    }

    @Override
    public void deleteInBatch(List<Long> ids) {
        reservationRepository.deleteAllByIdInBatch(ids);
    }


    public ReservationDto save(ReservationDto reservationDto) throws ElementNotFoundException {
        Reservation reservation = mapper.fromDto(reservationDto);
        Accommodation accommodation = accommodationService.findModelById(reservationDto.getAccommodationId());
        reservation.setAccommodation(accommodation);
        reservationRepository.save(reservation);
        return reservationDto;
    }

    @Override
    public ReservationDto saveNewReservation(ReservationDto reservationDto) {
        if (doesSameExist(reservationDto)){
            throw new ReservationAlreadyExistsException("You already made reservation for this dates for this accommodation");
        }
        Reservation reservation = mapper.fromDto(reservationDto);
        Accommodation accommodation = accommodationService.findModelById(reservationDto.getAccommodationId());
        reservation.setAccommodation(accommodation);
        reservation.setDateCreated(LocalDate.now());
        reservationRepository.save(reservation);
        sendNewReservationNotification(reservationDto);
        if (reservation.getReservationStatus().equals(ReservationStatus.Approved) || accommodation.isAutoApproval()) {
            accommodationService.reserveTimeslot(reservation.getAccommodation().getId(), reservation.getStartDate(), reservation.getStartDate().plusDays(reservation.getDays()));
            reservation.setReservationStatus(ReservationStatus.Approved);
            Runnable task1 = () -> setStatusToActive(reservation);
            Runnable task2 = () -> setStatusToDone(reservation);
            Instant endDate = reservation.getStartDate().plusDays(reservation.getDays()).atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant startDate = reservation.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC);
            LOG.log(Level.INFO, "Scheduled task to set reservation " + reservation.getId() + " to active on " + startDate);
            LOG.log(Level.INFO, "Scheduled task to set reservation " + reservation.getId() + " to done on " + endDate);
            taskScheduler.schedule(task1, startDate);
            taskScheduler.schedule(task2, endDate);
            sendRequestApprovedNotification(reservation);
        }else{
            Runnable task = () -> checkIfNotApproved(reservation);
            LOG.log(Level.INFO, "Scheduled task to set reservation "+ reservation.getId() + " to DENIED if not approved until " + reservation.getStartDate());

            taskScheduler.schedule(task, reservation.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC));
        }
        reservationRepository.save(reservation);
        LOG.log(Level.INFO, "I am leaving");

        return mapper.toDto(reservation);
    }

    private void sendNewReservationNotification(ReservationDto reservationDto){
        Account host = accountService.findModelById(reservationDto.getHostId());
        if (host.getSettings().contains(Settings.RESERVATION_REQUEST_NOTIFICATION)) {
            StringBuilder mess = new StringBuilder();
            mess.append("Guest ").append(accountService.findModelById(reservationDto.getGuestId()).getId()).append(" has created reservation request for your accommodation!");
            Notification notification = new Notification(null, mess.toString(), LocalDateTime.now(),host);
            notificationService.saveAndSendNotification(notification);
        }
    }

    private boolean doesSameExist(ReservationDto reservationDto){
        List<Reservation> reservations = reservationRepository.getIfExists(reservationDto.getStartDate(), reservationDto.getAccommodationId(),reservationDto.getGuestId());
        return !reservations.isEmpty();
    }


    @Override
    public ReservationDto update(ReservationDto reservationDto) throws ElementNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationDto.getId()).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        mapper.update(reservation, reservationDto);
        reservationRepository.save(reservation);
        return reservationDto;
    }

    public void delete(Long id) throws ElementNotFoundException {
        if (reservationRepository.existsById(id)){
            reservationRepository.deleteById(id);
        }else{
            throw  new ElementNotFoundException("Element with given ID doesn't exist!");
        }

    }

    @PostConstruct
    public void checkOnStartup(){
        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation: reservations){
            if (reservation.getStartDate().plusDays(reservation.getDays()).isBefore(LocalDate.now())){
                setStatusToDone(reservation);
            }
            if (reservation.getStartDate().isBefore(LocalDate.now())){
                setStatusToActive(reservation);
                checkIfNotApproved(reservation);
            }
        }
    }
}
