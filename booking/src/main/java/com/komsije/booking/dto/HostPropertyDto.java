package com.komsije.booking.dto;

import com.komsije.booking.model.Accommodation;
import com.komsije.booking.model.AccommodationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class HostPropertyDto {
    private Long id;
    private String name;
    private String location;
    private String hostName;
    private AccommodationStatus status;
    private String description;
    private Set<String> photos;

}
