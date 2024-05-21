package com.komsije.booking.controller;

import com.komsije.booking.dto.*;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.HasActiveReservationsException;
import com.komsije.booking.service.interfaces.GuestService;
import com.komsije.booking.validators.IdentityConstraint;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/guests")
@Validated

public class GuestController {
    private final GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/all")
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        List<GuestDto> guestsDtos = guestService.findAll();
        return new ResponseEntity<>(guestsDtos, HttpStatus.OK);
    }

//    @PreAuthorize("hasAnyRole('Admin','Host')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<GuestDto> getGuest(@IdentityConstraint @PathVariable Long id) {
        GuestDto guestDto = guestService.findById(id);
        return new ResponseEntity<>(guestDto, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('Guest', 'Admin')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteGuest(@IdentityConstraint @PathVariable Long id) {
        guestService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/favorites/{id}")
    public ResponseEntity<List<AccommodationDto>> getFavorites(@IdentityConstraint @PathVariable Long id){
        List<AccommodationDto> favorites = guestService.getFavoritesByGuestId(id);
        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Guest')")
    @PutMapping(value = "/favorites/{id}")
    public ResponseEntity<List<AccommodationDto>> addToFavorites(@IdentityConstraint @PathVariable Long id, @Valid @RequestBody AccommodationDto accommodationDto){
        List<AccommodationDto> favorites = guestService.addToFavorites(id, accommodationDto);
        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Guest', 'Admin')")
    @PutMapping(value = "/update", consumes = "application/json")
    public ResponseEntity<GuestDto> updateAccount(@Valid @RequestBody GuestDto guestDto){
        GuestDto guest = guestService.update(guestDto);
        return new ResponseEntity<>(guest, HttpStatus.OK);
    }

    @GetMapping(value = "/add-favorite")
    public ResponseEntity<Void> addFavorite(@IdentityConstraint @RequestParam Long guestId, @IdentityConstraint @RequestParam Long accommodationId){
        guestService.addFavorite(guestId, accommodationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/remove-favorite")
    public ResponseEntity<Void> removeFavorite(@IdentityConstraint @RequestParam Long guestId, @IdentityConstraint @RequestParam Long accommodationId){
        guestService.removeFavorite(guestId, accommodationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(value = "/check-favorite")
    public ResponseEntity<Boolean> checkFavorite(@IdentityConstraint @RequestParam Long guestId,@IdentityConstraint @RequestParam Long accommodationId){

        return new ResponseEntity<>(guestService.checkIfInFavorites(guestId, accommodationId),HttpStatus.OK);
    }


}
