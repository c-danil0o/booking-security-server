package com.komsije.booking.service.interfaces;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.GuestDto;
import com.komsije.booking.dto.HostDto;
import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.PendingReservationException;
import com.komsije.booking.model.Guest;
import com.komsije.booking.service.interfaces.crud.CrudService;

import java.util.List;
import java.util.Set;

public interface GuestService extends CrudService<GuestDto, Long> {
    public List<AccommodationDto> getFavoritesByGuestId(Long id) throws ElementNotFoundException;
    public List<AccommodationDto> addToFavorites(Long id, AccommodationDto accommodationDto) throws ElementNotFoundException;
    public Long singUpUser(RegistrationDto registrationDto);
    public void increaseCancelations(Long id) throws ElementNotFoundException;

    boolean cancelReservationRequest(Long id) throws ElementNotFoundException, PendingReservationException;

    void addFavorite(Long guestId, Long accommodationId);

    void removeFavorite(Long guestId, Long accommodationId);

    boolean checkIfInFavorites(Long guestId, Long accommodationId);
}
