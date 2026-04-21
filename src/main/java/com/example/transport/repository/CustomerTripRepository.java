package com.example.transport.repository;

import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.model.CustomerTrip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerTripRepository extends JpaRepository<CustomerTrip, Long> {

    @Query("SELECT COALESCE(SUM(ct.numberOfSeats), 0) FROM CustomerTrip ct WHERE ct.trip.id = :tripId")
    int sumSeatsByTrip(Long tripId);

    List<CustomerTrip> findByCustomerEmail(String email);

    @Query("""
    SELECT ct FROM CustomerTrip ct
    WHERE LOWER(ct.trip.departureLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(ct.trip.destinationLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<CustomerTrip> searchBookings(String keyword, Pageable pageable);

    @Query("""
SELECT new com.example.transport.dto.BookingResponseDTO(
    ct.bookingId,
    c.email,
    t.id,
    t.departureLocation,
    t.destinationLocation,
    t.departureDateTime,
    ct.numberOfSeats,
    t.price,
    ct.totalPrice,
    ct.status
)
FROM CustomerTrip ct
JOIN ct.trip t
JOIN ct.customer c
WHERE ct.deleted = false
""")
    Page<BookingResponseDTO> getAllBookingsOptimized(Pageable pageable);
}
