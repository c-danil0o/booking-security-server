package com.komsije.booking.service.interfaces;

import com.komsije.booking.dto.ReservationDto;
import com.komsije.booking.dto.ReservationViewDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.InvalidTimeSlotException;
import com.komsije.booking.exceptions.PendingReservationException;
import com.komsije.booking.model.ReservationStatus;
import com.komsije.booking.service.interfaces.crud.CrudService;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService extends CrudService<ReservationDto, Long> {
    public List<ReservationViewDto> getAll();
    public List<ReservationViewDto> getByHostId(Long id);
    public List<ReservationViewDto> getByGuestId(Long id);
    public List<ReservationViewDto> getRequestsByHostId(Long id);
    public List<ReservationViewDto> getRequestsByGuestId(Long id);

    public List<ReservationViewDto> getDecidedByHostId(Long id);
    public List<ReservationViewDto> getDecidedByGuestId(Long id);


    public List<ReservationDto> getByReservationStatus(ReservationStatus reservationStatus);

    public boolean hasActiveReservations(Long accountId);

    Integer getCancellationDeadline(Long reservationId);

    boolean hasHostActiveReservations(Long accountId);

    void restoreTimeslots(Long reservationId);

    public boolean overlappingActiveReservationsExist(LocalDate startDate, LocalDate endDate) throws InvalidTimeSlotException;
    public boolean deleteRequest(Long id) throws ElementNotFoundException, PendingReservationException;
    public ReservationDto updateStatus(Long id, ReservationStatus status) throws ElementNotFoundException;
    public boolean acceptReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException;
    public boolean denyReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException;
    public void deleteInBatch(List<Long> ids);
    ReservationDto saveNewReservation(ReservationDto reservationDto) throws ElementNotFoundException;
    // public boolean cancelReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException;



}
