package com.komsije.booking.controller;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.CertificateDto;
import com.komsije.booking.service.CertificateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/certificate")
public class CertificateController {
    private final CertificateServiceImpl certificateService;

    @Autowired
    public CertificateController(CertificateServiceImpl certificateService){
        this.certificateService = certificateService;
    }

    @GetMapping(value = "/download/{alias}")
    public ResponseEntity<CertificateDto> downloadPem(@PathVariable String alias) {
        return new ResponseEntity<>(this.certificateService.downloadCertificate(alias), HttpStatus.OK);
    }
}
