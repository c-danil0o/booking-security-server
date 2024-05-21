package com.komsije.booking.service.interfaces;

import com.komsije.booking.dto.HostDto;
import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.exceptions.ElementNotFoundException;
import com.komsije.booking.model.Host;
import com.komsije.booking.service.interfaces.crud.CrudService;

public interface HostService extends CrudService<HostDto, Long> {
    public Long singUpUser(RegistrationDto registrationDto);
}
