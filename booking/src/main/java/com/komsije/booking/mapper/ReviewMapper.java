package com.komsije.booking.mapper;

import com.komsije.booking.dto.ReservationDto;
import com.komsije.booking.dto.ReviewDto;
import com.komsije.booking.model.Reservation;
import com.komsije.booking.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserDtoMapper.class, HostMapper.class, AccommodationMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface ReviewMapper {
    ReviewDto toDto(Review review);
    Review fromDto(ReviewDto reviewDto);
    List<ReviewDto> toDto(List<Review> reviewList);
    void update(@MappingTarget Review review, ReviewDto reviewDto);
}