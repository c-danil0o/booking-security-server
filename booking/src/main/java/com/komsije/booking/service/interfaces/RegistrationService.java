package com.komsije.booking.service.interfaces;

import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.dto.TokenDto;

public interface RegistrationService {
    public Long register(RegistrationDto registrationDto);
    public String confirmToken(String token);
}
