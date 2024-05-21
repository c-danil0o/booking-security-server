package com.komsije.booking.dto;

import com.komsije.booking.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDto {
    @NotNull
    private AddressDto address;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String phone;
    @NotNull
    private Role role;
    @NotNull
    private Long id;
}
