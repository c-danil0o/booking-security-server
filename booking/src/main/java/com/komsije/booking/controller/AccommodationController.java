package com.komsije.booking.controller;

import com.komsije.booking.dto.*;
import com.komsije.booking.model.AccommodationStatus;
import com.komsije.booking.model.AccommodationType;
import com.komsije.booking.model.Reservation;
import com.komsije.booking.repository.ReservationRepository;
import com.komsije.booking.service.interfaces.AccommodationService;
import com.komsije.booking.validators.AccommodationTypeConstraint;
import com.komsije.booking.validators.IdentityConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import org.checkerframework.common.value.qual.IntVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/accommodations")
@Validated
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Autowired
    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/all")
    public ResponseEntity<List<AccommodationDto>> getAllAccommodations() {
        List<AccommodationDto> accommodations = accommodationService.findAll();
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @GetMapping(value = "/type")
    public ResponseEntity<List<AccommodationDto>> getByAccommodationType(@AccommodationTypeConstraint @RequestParam String type) {
        List<AccommodationDto> accommodations = accommodationService.getByAccommodationType(AccommodationType.valueOf(type));
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<AccommodationDto> getAccommodation(@IdentityConstraint @PathVariable Long id) {
        AccommodationDto accommodation = accommodationService.findById(id);

        return new ResponseEntity<>(accommodation, HttpStatus.OK);
    }

    @GetMapping(value = "/host/{hostId}")
    public ResponseEntity<List<HostPropertyDto>> getByHostId(@IdentityConstraint @PathVariable Long hostId) {
        List<HostPropertyDto> accommodations = accommodationService.findByHostId(hostId);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @GetMapping(value = "/all/short")
    public ResponseEntity<List<AccommodationShortDto>> getAllDisplay() {
        List<AccommodationShortDto> accommodations = accommodationService.getAllShort();
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/unapproved")
    public ResponseEntity<List<HostPropertyDto>> getUnapproved() {
        List<HostPropertyDto> accommodations = accommodationService.getUnapprovedAccommodations();
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<AccommodationDto>> getByLocationGuestNumberAndDate(@NotEmpty @RequestParam(required = false) String location, @RequestParam(required = false) Integer guests, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate startDate, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate endDate) {
        List<AccommodationDto> accommodations = accommodationService.getByLocationNumOfGuestsAndDate(location, guests, startDate, endDate);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @GetMapping(value = "/amenities")
    public ResponseEntity<List<AccommodationDto>> getByAmenities(@NotNull @RequestParam List<String> amenities) {
        List<AccommodationDto> accommodations = accommodationService.getByAmenities(amenities);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Host')")
    @GetMapping(value = "/yearAnalytics/{hostId}/{year}")
    public ResponseEntity<List<AccommodationAnalysis>> getYearAnalytics(@IdentityConstraint @PathVariable("hostId") Long hostId,@NotNull @PathVariable("year") int year) {
        List<AccommodationAnalysis> accommodations = accommodationService.getYearAnalytics(hostId, year);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Host')")
    @GetMapping(value = "/analytics/{hostId}")
    public ResponseEntity<List<AccommodationTotalEarnings>> getPeriodAnalytics(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @PathVariable("hostId") Long hostId) {
        List<AccommodationTotalEarnings> accommodations = accommodationService.getPeriodAnalytics(hostId, startDate, endDate);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Host','Admin')")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<AccommodationDto> saveAccommodation(@RequestBody AccommodationDto accommodationDTO) {
        AccommodationDto accommodation = accommodationService.save(accommodationDTO);
        return new ResponseEntity<>(accommodation, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('Host', 'Admin')")
    @PatchMapping(value = "/{id}/approve")
    public ResponseEntity<AccommodationDto> approveAccommodation(@IdentityConstraint @PathVariable("id") Long id) {
        AccommodationDto accommodationDto = accommodationService.findById(id);
        accommodationDto.setStatus(AccommodationStatus.Active);
        accommodationDto = accommodationService.update(accommodationDto);
        return new ResponseEntity<>(accommodationDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Host', 'Admin')")
    @PatchMapping(value = "/{id}/deny")
    public ResponseEntity<AccommodationDto> denyAccommodation(@IdentityConstraint @PathVariable("id") Long id) {
        AccommodationDto accommodationDto = accommodationService.findById(id);
        accommodationDto.setStatus(AccommodationStatus.Inactive);
        accommodationDto = accommodationService.update(accommodationDto);
        return new ResponseEntity<>(accommodationDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Host', 'Admin')")
    @PatchMapping(value = "/{id}/changeApproval")
    public ResponseEntity<AccommodationDto> changeApproval(@IdentityConstraint @PathVariable("id") Long id, @NotNull @RequestParam boolean auto) {
        AccommodationDto accommodationDto = accommodationService.findById(id);
        accommodationDto.setAutoApproval(auto);
        accommodationDto = accommodationService.update(accommodationDto);
        return new ResponseEntity<>(accommodationDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Host','Admin')")
    @PutMapping(value = "/update")
    public ResponseEntity<AccommodationDto> updateAccommodation(@RequestBody AccommodationDto accommodationDto) {
        AccommodationDto resultAccommodation = accommodationService.update(accommodationDto);
        return new ResponseEntity<>(resultAccommodation, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Host')")
    @PutMapping(value = "/add-availability/{id}")
    public ResponseEntity<AccommodationDto> updateAvailability(@IdentityConstraint @PathVariable("id") Long id, @RequestBody AvailabilityDto availabilityDto) {
        AccommodationDto accommodationDto = accommodationService.updateAvailability(id, availabilityDto);
        return new ResponseEntity<>(accommodationDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Host','Admin')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAccommodation(@IdentityConstraint @PathVariable Long id) {
        accommodationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/search" , consumes = "application/json")
    public ResponseEntity<List<SearchResponseDto>> searchAccommodations(@RequestBody SearchRequestDto searchRequestDto) {
        List<SearchResponseDto> accommodations = accommodationService.getSearchedAccommodations(searchRequestDto);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @PostMapping(value="/get-availability-price", consumes = "application/json")
    public ResponseEntity<PriceResponse> getAvailabilityPrice(@RequestBody PriceRequest priceRequest) {
        PriceResponse priceResponse = accommodationService.getAvailabilityPrice(priceRequest);
        return new ResponseEntity<>(priceResponse, HttpStatus.OK);
    }
}
