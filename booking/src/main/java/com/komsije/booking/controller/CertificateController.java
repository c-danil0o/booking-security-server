package com.komsije.booking.controller;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.CertificateDto;
import com.komsije.booking.dto.SignedCertificateDto;
import com.komsije.booking.service.CertificateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/certificate")
public class CertificateController {
    private final CertificateServiceImpl certificateService;

    @Autowired
    public CertificateController(CertificateServiceImpl certificateService){
        this.certificateService = certificateService;
    }

    @PostMapping(value = "/check")
    public ResponseEntity<CertificateDto> downloadPem(@RequestBody SignedCertificateDto certificate) {
        return new ResponseEntity<>(this.certificateService.checkCertificate(certificate), HttpStatus.OK);
    }
}
