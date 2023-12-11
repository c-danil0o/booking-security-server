package com.komsije.booking.controller;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.ReviewDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.HasActiveReservationsException;
import com.komsije.booking.model.AccommodationType;
import com.komsije.booking.model.Review;
import com.komsije.booking.service.AccountServiceImpl;
import com.komsije.booking.service.ReviewServiceImpl;
import com.komsije.booking.service.interfaces.AccountService;
import com.komsije.booking.service.interfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    private final AccountService accountService;

    @Autowired
    public ReviewController(ReviewService reviewService, AccountService accountService) {
        this.reviewService = reviewService;
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/all")
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviewDtos = reviewService.findAll();
        return new ResponseEntity<>(reviewDtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {

        ReviewDto reviewDto = null;
        try {
            reviewDto = reviewService.findById(id);
        } catch (ElementNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(reviewDto, HttpStatus.OK);
    }
    @GetMapping(value = "/approved")
    public ResponseEntity<List<ReviewDto>> getApprovedReviews(){
        try{
            List<ReviewDto> reviewDtos = reviewService.getApprovedReviews();

            return new ResponseEntity<>(reviewDtos, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ReviewDto> saveReview(@RequestBody ReviewDto reviewDTO) {
        ReviewDto reviewDto = null;
        try {
            reviewDto = reviewService.save(reviewDTO);
        } catch (ElementNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reviewDto, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}/approve")
    public ResponseEntity<ReviewDto> approveReview(@PathVariable("id") Long id) {

        ReviewDto reviewDto = null;
        try {
            reviewService.setApproved(id);
            reviewDto = reviewService.findById(id);
        } catch (ElementNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reviewDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {

        try {
            reviewService.delete(id);
        } catch (HasActiveReservationsException | ElementNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getByAccommodationId(@RequestParam Long accommodationId ) {
        try {
            List<ReviewDto> reviewDtos = reviewService.findByAccommodationId(accommodationId);
            return new ResponseEntity<>(reviewDtos, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ElementNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/host")
    public ResponseEntity<List<ReviewDto>> getByHostId(@RequestParam Long hostId ) {
        try {
            List<ReviewDto> reviewDtos = reviewService.findByHostId(hostId);
            return new ResponseEntity<>(reviewDtos, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ElementNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
