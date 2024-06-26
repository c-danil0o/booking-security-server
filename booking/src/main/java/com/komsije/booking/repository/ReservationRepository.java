package com.komsije.booking.repository;

import com.komsije.booking.model.Reservation;
import com.komsije.booking.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByReservationStatus(ReservationStatus reservationStatus);

    @Query("select r from Reservation r where r.hostId=:id")
    List<Reservation> findByHostId(@Param("id") Long id);

    @Query("select r from Reservation r where r.guestId=:id")
    List<Reservation> findByGuestId(@Param("id") Long id);

    @Query("select r from Reservation r where r.accommodation.id=:accommodationId and r.reservationStatus='Done'")
    List<Reservation> findDoneByAccommodationId(@Param("accommodationId") Long hostId);

    @Query("select r from Reservation r where (r.hostId=:hostId or r.guestId=:hostId) and (r.guestId=:guestId or r.hostId=:guestId) and r.reservationStatus='Done'")
    List<Reservation> findDoneByHostIdAndGuestId(@Param("hostId") Long hostId, @Param("guestId") Long guestId);

    @Query("select r from Reservation r where r.accommodation.id=:accommodationId and r.reservationStatus='Pending'")
    List<Reservation> findPendingByAccommodationId(@Param("accommodationId") Long accommodationId);

    @Query("select r from Reservation r where r.startDate=:startDate and r.accommodation.id=:accommodationId and r.guestId=:guestId and r.reservationStatus!='Cancelled'")
    List<Reservation> getIfExists(@Param("startDate")LocalDate startDate, @Param("accommodationId") Long accommodationId, @Param("guestId") Long guestId);

    @Query("select r from Reservation r where r.accommodation.id=:accommodationId and r.reservationStatus='Done' and year(r.startDate)=:year")
    List<Reservation> findDoneByAccommodationAndYear(@Param("accommodationId") Long accommodationId, @Param("year") int year);

}
