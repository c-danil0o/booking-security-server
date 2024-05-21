package com.komsije.booking.mapper;

import com.komsije.booking.dto.GuestDto;
import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.model.*;
import com.komsije.booking.repository.GuestRepository;
import com.komsije.booking.repository.HostRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, AccommodationMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public abstract class GuestMapper {
    @Autowired
    private GuestRepository guestRepository;
    public abstract GuestDto toDto(Guest guest);
    public Guest fromDto(GuestDto guestDto){
        if (guestDto.getId()!= null && !guestRepository.existsById(guestDto.getId())){
            Guest guest = new Guest();
            guest.setRole(Role.Guest);
            guest.setAddress(new Address(null, guestDto.getAddress().getStreet(), guestDto.getAddress().getCity(), guestDto.getAddress().getNumber(),guestDto.getAddress().getCountry(), guestDto.getAddress().getLatitude(), guestDto.getAddress().getLongitude()));
            guest.setFirstName(guestDto.getFirstName());
            guest.setLastName(guestDto.getLastName());
            guest.setPhone(guestDto.getPhone());
            guest.setTimesCancelled(guestDto.getTimesCancelled());
            return guest;
        }else{
            return guestRepository.findById(guestDto.getId()).orElse(null);
        }

    }
    public abstract List<GuestDto> toDto(List<Guest> guestsList);
    public abstract void update(@MappingTarget Guest guest, GuestDto guestDto);

    public Guest fromRegistrationDto(RegistrationDto registrationDto){
        Guest guest = new Guest();
        guest.setId(registrationDto.getId());
        guest.setSettings(new HashSet<>(List.of(Settings.RESERVATION_RESPONSE_NOTIFICATION)));
        guest.setPhone(registrationDto.getPhone());
        guest.setAddress(new Address(null, registrationDto.getAddress().getStreet(), registrationDto.getAddress().getCity(), registrationDto.getAddress().getNumber(),registrationDto.getAddress().getCountry(), null, null));
        guest.setFirstName(registrationDto.getFirstName());
        guest.setLastName(registrationDto.getLastName());
        guest.setTimesCancelled(0);
        guest.setRole(Role.Guest);
        return guest;
    }
}
