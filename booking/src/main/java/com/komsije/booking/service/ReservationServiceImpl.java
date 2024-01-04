package com.komsije.booking.service;

import com.komsije.booking.dto.NewReservationDto;
import com.komsije.booking.dto.ReservationDto;
import com.komsije.booking.dto.ReservationViewDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.InvalidTimeSlotException;
import com.komsije.booking.exceptions.PendingReservationException;
import com.komsije.booking.mapper.ReservationMapper;
import com.komsije.booking.model.Accommodation;
import com.komsije.booking.model.Reservation;
import com.komsije.booking.model.ReservationStatus;
import com.komsije.booking.repository.ReservationRepository;
import com.komsije.booking.service.interfaces.AccommodationService;
import com.komsije.booking.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationMapper mapper;
    private final ReservationRepository reservationRepository;
    private final AccommodationService accommodationService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, AccommodationService accommodationService) {
        this.reservationRepository = reservationRepository;
        this.accommodationService = accommodationService;
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
    public void saveModel(Reservation reservation) {
        this.reservationRepository.save(reservation);
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
        if(reservation.getReservationStatus().equals(ReservationStatus.Pending) || reservation.getReservationStatus().equals(ReservationStatus.Denied)){
            reservation.setReservationStatus(ReservationStatus.Approved);
            accommodationService.reserveTimeslot(reservation.getAccommodation().getId(),reservation.getStartDate(), reservation.getStartDate().plusDays(reservation.getDays()));

            reservationRepository.save(reservation);
        }else{
            throw new PendingReservationException("Reservation is not in pending or denied state!");
        }
        LocalDate startDate = reservation.getStartDate();
        LocalDate endDate = reservation.getStartDate().plusDays(reservation.getDays());
        List<Reservation> reservations = reservationRepository.findReservationsByReservationStatus(ReservationStatus.Pending);
        for(Reservation res: reservations){
            if (startDate.isBefore(reservation.getStartDate().plusDays(reservation.getDays()))&& reservation.getStartDate().isBefore(endDate)){
                res.setReservationStatus(ReservationStatus.Denied);
                reservationRepository.save(res);
            }
        }
        return true;
    }

    @Override
    public boolean denyReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->  new ElementNotFoundException("Element with given ID doesn't exist!"));
        if(reservation.getReservationStatus().equals(ReservationStatus.Pending) || reservation.getReservationStatus().equals(ReservationStatus.Approved)){
            reservation.setReservationStatus(ReservationStatus.Denied);
            reservationRepository.save(reservation);
        }else{
            throw new PendingReservationException("Reservation is not in pending or approved state!");
        }
//        todo: update accommodations if reservation was approved
        return true;
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
    public ReservationDto saveNewReservation(NewReservationDto reservationDto) throws ElementNotFoundException {
        Reservation reservation = mapper.fromNewDto(reservationDto);
        Accommodation accommodation = accommodationService.findModelById(reservationDto.getAccommodationId());
        reservation.setAccommodation(accommodation);
        reservation.setDateCreated(LocalDate.now());
        reservationRepository.save(reservation);
        if (reservation.getReservationStatus().equals(ReservationStatus.Approved)){
            accommodationService.reserveTimeslot(reservation.getAccommodation().getId(),reservation.getStartDate(), reservation.getStartDate().plusDays(reservation.getDays()));
        }
        return mapper.toDto(reservation);
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

}
