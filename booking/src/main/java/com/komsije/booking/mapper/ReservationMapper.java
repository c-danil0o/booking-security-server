package com.komsije.booking.mapper;

import com.komsije.booking.dto.ReservationDto;
import com.komsije.booking.dto.ReservationViewDto;
import com.komsije.booking.model.Accommodation;
import com.komsije.booking.model.Reservation;
import com.komsije.booking.repository.GuestRepository;
import com.komsije.booking.repository.HostRepository;
import com.komsije.booking.repository.ReservationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses={GuestMapper.class} , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ReservationMapper {
    @Autowired
    private HostRepository hostRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    public ReservationDto toDto(Reservation reservation){
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setStartDate(reservation.getStartDate());
        reservationDto.setDays(reservation.getDays());
        reservationDto.setReservationStatus(reservation.getReservationStatus());
        reservationDto.setAccommodationId(reservation.getAccommodation().getId());
        reservationDto.setGuestId(reservation.getGuestId());
        reservationDto.setHostId(reservation.getHostId());
        reservationDto.setDateCreated(reservation.getDateCreated());
        reservationDto.setPrice(reservation.getPrice());
        reservationDto.setNumberOfGuests(reservation.getNumberOfGuests());
        return reservationDto;
    }
    public Reservation fromDto(ReservationDto reservationDto) {
        if ( reservationDto == null ) {
            return null;
        }
        if (reservationDto.getId()!=null){
            if (reservationRepository.existsById(reservationDto.getId())){
                return reservationRepository.findById(reservationDto.getId()).orElse(null);
            }
        }
        Reservation reservation = new Reservation();

        reservation.setId(reservationDto.getId());
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setDateCreated(reservationDto.getDateCreated());
        reservation.setDays(reservationDto.getDays());
        reservation.setNumberOfGuests(reservationDto.getNumberOfGuests());
        reservation.setPrice(reservationDto.getPrice());
        reservation.setHostId(reservationDto.getHostId());
        reservation.setGuestId(reservationDto.getGuestId());
        reservation.setReservationStatus(reservationDto.getReservationStatus());

        return reservation;


    }
    public List<ReservationDto> toDto(List<Reservation> reservationList){
        List<ReservationDto> reservationDtos = new ArrayList<>();
        for (Reservation reservation:
             reservationList) {
            reservationDtos.add(toDto(reservation));
        }
        return reservationDtos;
    }
    public abstract void update(@MappingTarget Reservation reservation, ReservationDto reservationDto);

    public ReservationViewDto toViewDto(Reservation reservation){
        ReservationViewDto reservationDto = new ReservationViewDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setStartDate(reservation.getStartDate().atStartOfDay());
        reservationDto.setEndDate(reservation.getStartDate().plusDays(reservation.getDays()).atStartOfDay());
        reservationDto.setPrice(reservation.getPrice());
        Accommodation accommodation = reservation.getAccommodation();
        reservationDto.setAccommodationName(accommodation.getName()+" , "+ accommodation.getAddress().getCity());
        reservationDto.setGuestEmail(guestRepository.getReferenceById(reservation.getGuestId()).getId().toString());
        reservationDto.setHostEmail(hostRepository.getReferenceById(reservation.getHostId()).getId().toString());
        reservationDto.setAccommodationId(accommodation.getId());
        reservationDto.setHostId(reservation.getHostId());
        reservationDto.setGuestId(reservation.getGuestId());
        reservationDto.setReservationStatus(reservation.getReservationStatus());
        reservationDto.setNumberOfGuests(reservation.getNumberOfGuests());
        reservationDto.setGuestTimesCancelled(guestRepository.getReferenceById(reservation.getGuestId()).getTimesCancelled());
        return reservationDto;
    }
    public List<ReservationViewDto> toViewDto(List<Reservation> reservationList){
        List<ReservationViewDto> reservationDtos = new ArrayList<>();
        for (Reservation reservation:
                reservationList) {
            reservationDtos.add(toViewDto(reservation));
        }
        return reservationDtos;
    }
}
