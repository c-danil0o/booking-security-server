package com.komsije.booking.mapper;

import com.komsije.booking.dto.HostDto;
import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.model.Address;
import com.komsije.booking.model.Host;
import com.komsije.booking.model.Role;
import com.komsije.booking.model.Settings;
import com.komsije.booking.repository.HostRepository;
import com.komsije.booking.service.interfaces.HostService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public abstract class HostMapper {
    @Autowired
    private HostRepository hostRepository;
    public abstract HostDto toDto(Host host);
    public Host fromDto(HostDto hostDto){
        if (hostDto.getId() != null && !hostRepository.existsById(hostDto.getId())){


        Host host = new Host();
        host.setRole(Role.Host);
        host.setAddress(new Address(null, hostDto.getAddress().getStreet(), hostDto.getAddress().getCity(), hostDto.getAddress().getNumber(),hostDto.getAddress().getCountry(), hostDto.getAddress().getLatitude(), hostDto.getAddress().getLongitude()));
        host.setFirstName(hostDto.getFirstName());
        host.setLastName(hostDto.getLastName());
        host.setPhone(hostDto.getPhone());
        return host;
        }else{
            return hostRepository.findById(hostDto.getId()).orElse(null);
        }
    }
    public abstract List<HostDto> toDto(List<Host> hostList);
    public abstract void update(@MappingTarget Host host, HostDto hostDto);
    public Host fromRegistrationDto(RegistrationDto registrationDto){
        Host host = new Host();
        host.setId(registrationDto.getId());
        host.setSettings(new HashSet<>(List.of(Settings.HOST_REVIEW_NOTIFICATION, Settings.ACCOMMODATION_REVIEW_NOTIFICATION, Settings.RESERVATION_CANCEL_NOTIFICATION, Settings.RESERVATION_REQUEST_NOTIFICATION)));
        host.setPhone(registrationDto.getPhone());
        host.setAddress(new Address(null, registrationDto.getAddress().getStreet(), registrationDto.getAddress().getCity(), registrationDto.getAddress().getNumber(), registrationDto.getAddress().getCountry(), null, null));
        host.setFirstName(registrationDto.getFirstName());
        host.setLastName(registrationDto.getLastName());
        host.setRole(Role.Host);
        return host;
    }
}
