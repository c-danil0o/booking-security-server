package com.komsije.booking.mapper;

import com.komsije.booking.dto.AccommodationDto;
import com.komsije.booking.dto.AvailabilityDto;
import com.komsije.booking.dto.TimeSlotDto;
import com.komsije.booking.model.Accommodation;
import com.komsije.booking.model.Address;
import com.komsije.booking.model.TimeSlot;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, TimeSlotMapper.class, HostMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public abstract class AccommodationMapper {
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private TimeSlotMapper timeSlotMapper;
    public abstract AccommodationDto toDto(Accommodation accommodation);

    public abstract Accommodation fromDto(AccommodationDto accommodationDto);

    public abstract List<AccommodationDto> toDto(List<Accommodation> accommodationList);
    public abstract void update(@MappingTarget Accommodation accommodation, AccommodationDto accommodationDto);

    /*public void update(@MappingTarget Accommodation accommodation, AccommodationDto accommodationDto) {
        if (accommodationDto == null) {
            return;
        }

        accommodation.setId(accommodationDto.getId());
        accommodation.setName(accommodationDto.getName());
        accommodation.setDescription(accommodationDto.getDescription());
        if (accommodationDto.getAddress() != null) {
            accommodation.setAddress(new Address());

            addressMapper.update(accommodation.getAddress(), accommodationDto.getAddress());
        }
        accommodation.setAccommodationType(accommodationDto.getAccommodationType());
        if (accommodation.getAmenities() != null) {
            Set<String> set = accommodationDto.getAmenities();
            if (set != null) {
                accommodation.getAmenities().clear();
                accommodation.getAmenities().addAll(set);
            } else {
                accommodation.setAmenities(null);
            }
        } else {
            Set<String> set = accommodationDto.getAmenities();
            if (set != null) {
                accommodation.setAmenities(new LinkedHashSet<String>(set));
            }
        }
        if (accommodation.getAvailability() != null) {
            Set<TimeSlot> set1 = timeSlotDtoSetToTimeSlotSet(accommodationDto.getAvailability());
            if (set1 != null) {
                accommodation.getAvailability().clear();
                accommodation.getAvailability().addAll(set1);
            } else {
                accommodation.setAvailability(null);
            }
        } else {
            Set<TimeSlot> set1 = timeSlotDtoSetToTimeSlotSet(accommodationDto.getAvailability());
            if (set1 != null) {
                accommodation.setAvailability(set1);
            }
        }
        accommodation.setMaxGuests(accommodationDto.getMaxGuests());
        accommodation.setMinGuests(accommodationDto.getMinGuests());
        if (accommodation.getPhotos() != null) {
            Set<String> set2 = accommodationDto.getPhotos();
            if (set2 != null) {
                accommodation.getPhotos().clear();
                accommodation.getPhotos().addAll(set2);
            } else {
                accommodation.setPhotos(null);
            }
        } else {
            Set<String> set2 = accommodationDto.getPhotos();
            if (set2 != null) {
                accommodation.setPhotos(new LinkedHashSet<String>(set2));
            }
        }
        accommodation.setPricePerGuest(accommodationDto.isPricePerGuest());
        accommodation.setCancellationDeadline(accommodationDto.getCancellationDeadline());
        accommodation.setAutoApproval(accommodationDto.isAutoApproval());
        accommodation.setAverageGrade(accommodationDto.getAverageGrade());
        accommodation.setApproved(accommodationDto.isApproved());
    }
    protected Set<TimeSlotDto> timeSlotSetToTimeSlotDtoSet(Set<TimeSlot> set) {
        if ( set == null ) {
            return null;
        }

        Set<TimeSlotDto> set1 = new LinkedHashSet<TimeSlotDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( TimeSlot timeSlot : set ) {
            set1.add( timeSlotMapper.toDto( timeSlot ) );
        }

        return set1;
    }

    protected Set<TimeSlot> timeSlotDtoSetToTimeSlotSet(Set<TimeSlotDto> set) {
        if ( set == null ) {
            return null;
        }

        Set<TimeSlot> set1 = new LinkedHashSet<TimeSlot>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( TimeSlotDto timeSlotDto : set ) {
            set1.add( timeSlotMapper.fromDto( timeSlotDto ) );
        }

        return set1;
    }*/

    public void update(@MappingTarget Accommodation accommodation, AvailabilityDto availabilityDto) {
        if (availabilityDto.getCancellationDeadline() != null)
            accommodation.setCancellationDeadline(availabilityDto.getCancellationDeadline());
        for (TimeSlotDto timeSlotDto : availabilityDto.getAvailability()) {
            accommodation.getAvailability().add(new TimeSlot(null, timeSlotDto.getStartDate(), timeSlotDto.getEndDate(), timeSlotDto.getPrice(), timeSlotDto.isOccupied()));
        }
    }
}