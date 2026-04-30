package com.example.transport.service;

import com.example.transport.dto.BookingRequestDTO;
import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.payload.PagedResponse;
import com.example.transport.dto.UpdateBookingRequestDTO;
import com.example.transport.enums.BookingStatus;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.model.CustomerTrip;
import com.example.transport.model.Trip;
import com.example.transport.model.User;
import com.example.transport.repository.CustomerTripRepository;
import com.example.transport.repository.TripRepository;
import com.example.transport.repository.UserRepository;
import com.example.transport.repository.specification.BookingSearchSpecs;
import com.example.transport.util.CacheKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final CustomerTripRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private BookingResponseDTO mapToResponse(CustomerTrip booking) {

        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .customerEmail(
                        booking.getCustomer() != null ? booking.getCustomer().getEmail() : null
                )
                .tripId(
                        booking.getTrip() != null ? booking.getTrip().getTripId() : null
                )
                .departureLocation(
                        booking.getTrip() != null ? booking.getTrip().getDepartureLocation() : null
                )
                .departureDateTime(
                        booking.getTrip() != null ? booking.getTrip().getDepartureDateTime() : null
                )
                .destinationLocation(
                        booking.getTrip() != null ? booking.getTrip().getDestinationLocation() : null
                )
                .numberOfSeats(booking.getNumberOfSeats())
                .pricePerSeat(
                        booking.getTrip() != null ? booking.getTrip().getPrice() : null
                )
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }

    @Override
    @CacheEvict(value = CacheKeys.BOOKING, allEntries = true)
    public BookingResponseDTO createBooking(BookingRequestDTO request) {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        int bookedSeats = bookingRepository.sumSeatsByTrip(trip.getTripId());
        int availableSeats = trip.getTotalNoOfPassengers() - bookedSeats;

        if (request.getNumberOfSeats() > availableSeats) {
            throw new RuntimeException("Not enough seats available");
        }

        BigDecimal totalPrice = trip.getPrice()
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        CustomerTrip booking = CustomerTrip.builder()
                .customer(user)
                .trip(trip)
                .numberOfSeats(request.getNumberOfSeats())
                .totalPrice(totalPrice)
                .status(BookingStatus.BOOKED)
                .build();

        bookingRepository.save(booking);
        emailService.sendCreatedBooking(booking);

        return mapToResponse(booking);

    }

    @Override
    @CacheEvict(value = CacheKeys.BOOKING, allEntries = true)
    public void cancelBooking(Long id) {

        CustomerTrip booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Cacheable(value = CacheKeys.BOOKING, key = "#id")
    public BookingResponseDTO getBooking(Long id) {

        CustomerTrip booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    @Cacheable(value = CacheKeys.BOOKING, key = "#page + '-' + #size + '-' + #sortBy")
    public PagedResponse<BookingResponseDTO> getPagedBookings(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<BookingResponseDTO> bookingsPage = bookingRepository.getAllBookingsOptimized(pageable);
        return new PagedResponse<>(bookingsPage);
    }

    @Override
    @CacheEvict(value = CacheKeys.BOOKING, allEntries = true)
    public BookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request) {

        CustomerTrip booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot update cancelled booking");
        }

        booking.setNumberOfSeats(request.getNumberOfSeats());

        BigDecimal totalPrice = booking.getTrip().getPrice()
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        booking.setTotalPrice(totalPrice);

        CustomerTrip updated = bookingRepository.save(booking);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public List<BookingResponseDTO> getMyBookings(String email) {

        List<CustomerTrip> bookings = bookingRepository.findByCustomerEmail(email);

        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<BookingResponseDTO> searchBookings(
            String keyword,
            BigDecimal totalPrice,
            String status,
            Long tripId,
            Pageable pageable) {

        Specification<CustomerTrip> spec = Specification.allOf();

        if (keyword != null && keyword.length() >= 3) {
            spec = spec.and(BookingSearchSpecs.keywordSearch(keyword));
        }
        if (totalPrice != null) {
            spec = spec.and(BookingSearchSpecs.hasTotalPrice(totalPrice));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(BookingSearchSpecs.hasStatus(status));
        }
        if (tripId != null) {
            spec = spec.and(BookingSearchSpecs.hasTripId(tripId));
        }

        Page<CustomerTrip> bookingPage = bookingRepository.findAll(spec, pageable);
        return bookingPage.map(this::mapToResponse);
    }
}

