package com.komsije.booking.dto;

import com.komsije.booking.model.Address;
import com.komsije.booking.model.Host;
import com.komsije.booking.validators.IdentityConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
public class HostDto {
    private Long id;
    @Email
    private String email;
    private boolean isBlocked;
    @NotNull
    private AddressDto address;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String phone;
    private String alias;
}
