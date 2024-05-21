package com.komsije.booking.repository;

import com.komsije.booking.model.Guest;
import com.komsije.booking.model.Host;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

}
