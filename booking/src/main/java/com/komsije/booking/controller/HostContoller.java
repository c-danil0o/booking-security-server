package com.komsije.booking.controller;

import com.komsije.booking.dto.AccountDto;
import com.komsije.booking.dto.EmailDto;
import com.komsije.booking.dto.HostDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.exceptions.HasActiveReservationsException;
import com.komsije.booking.model.Host;
import com.komsije.booking.service.interfaces.HostService;
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
@RequestMapping(value = "api/hosts")
public class HostContoller {
    private final HostService hostService;

    @Autowired
    public HostContoller(HostService hostService) {
        this.hostService = hostService;
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(value = "/all")
    @Validated

    public ResponseEntity<List<HostDto>> getAllHosts() {
        List<HostDto> hostDtos = hostService.findAll();
        return new ResponseEntity<>(hostDtos, HttpStatus.OK);
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<HostDto> getHost(@IdentityConstraint  @PathVariable Long id) {
        HostDto hostDto = hostService.findById(id);
        return new ResponseEntity<>(hostDto, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('Host', 'Admin')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteHost(@IdentityConstraint @PathVariable Long id) {
        hostService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('Host', 'Admin')")
    @PutMapping(value = "/update", consumes = "application/json")
    public ResponseEntity<HostDto> updateAccount(@Valid @RequestBody HostDto hostDto){
        HostDto host = hostService.update(hostDto);
        return new ResponseEntity<>(host, HttpStatus.OK);
    }



}
