package com.example.transport.service;

import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.dto.CreateBookingDTO;
import com.example.transport.mapper.BookingMapper;
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
import com.example.transport.repository.specification.GenericSearchSpecification;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final CustomerTripRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.BOOKING, allEntries = true)
    public BookingResponseDTO createBooking(CreateBookingDTO request, UserDetails userDetails) {

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        Integer bookedSeats = bookingRepository.sumSeatsByTrip(trip.getTripId());
        int safeBookedSeats = (bookedSeats == null) ? 0 : bookedSeats;

//        int availableSeats = trip.getTotalNoOfPassengers() - safeBookedSeats;
//
//        if (request.getNumberOfSeats() > availableSeats) {
//            throw new RuntimeException("Not enough seats available");
//        }

        trip.setTotalNoOfPassengers(trip.getTotalNoOfPassengers()); // trigger version check
        if (trip.getBookedSeats() + request.getNumberOfSeats() > trip.getTotalNoOfPassengers()) {
            throw new RuntimeException("Not enough seats");
        }

        trip.setBookedSeats(trip.getBookedSeats() + request.getNumberOfSeats());

        BigDecimal totalPrice = trip.getPrice()
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        CustomerTrip booking = CustomerTrip.builder()
                .customer(user)
                .trip(trip)
                .numberOfSeats(request.getNumberOfSeats())
                .totalPrice(totalPrice)
                .status(BookingStatus.BOOKED)
                .build();

        CustomerTrip savedBooking = bookingRepository.save(booking);

        try {
            emailService.sendCreatedBooking(savedBooking);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }

        return BookingMapper.toDTO(savedBooking);
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

        return BookingMapper.toDTO(booking);
    }

    @Override
    @Transactional
    @Cacheable(value = CacheKeys.BOOKING, key = "#page + '-' + #size + '-' + #sortBy")
    public PagedResponse<BookingResponseDTO> getPagedBookings(
            int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<CustomerTrip> bookingPage =
                bookingRepository.findAllWithRelations(pageable);

        Page<BookingResponseDTO> dtoPage =
                bookingPage.map(BookingMapper::toDTO);

        return new PagedResponse<>(dtoPage);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheKeys.BOOKING, allEntries = true)
    public BookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request) {

        CustomerTrip booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot update cancelled booking");
        }

        if (request.getNumberOfSeats() == null || request.getNumberOfSeats() <= 0) {
            throw new IllegalArgumentException("Number of seats must be greater than 0");
        }

        if (booking.getTrip() == null) {
            throw new RuntimeException("Booking has no associated trip");
        }

        booking.setNumberOfSeats(request.getNumberOfSeats());

        BigDecimal totalPrice = booking.getTrip().getPrice()
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        booking.setTotalPrice(totalPrice);

        CustomerTrip updated = bookingRepository.save(booking);

        return BookingMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public Page<BookingResponseDTO> getMyBookings(String email, Pageable pageable) {

        Page<CustomerTrip> bookings =
                bookingRepository.findByCustomerEmail(email, pageable);

        return bookings.map(BookingMapper::toDTO);
    }

    @Override
    public Page<BookingResponseDTO> searchBookings(
            String keyword,
            Long bookingId,
            BigDecimal totalPrice,
            String status,

            Long userId,
            String firstName,
            String lastName,
            String email,
            String phoneNo,

            Long tripId,
            LocalDate departureDateTime,
            String departureLocation,
            String destinationLocation,
            BigDecimal price,
            Pageable pageable
    ) {

        Map<String, Object> filters = new HashMap<>();

        if (bookingId != null) {
            filters.put("bookingId", bookingId);
        }

        if (totalPrice != null) {
            filters.put("totalPrice", totalPrice);
        }

        if (status != null && !status.isBlank()) {
            try {
                filters.put("status", BookingStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        // customer search params

        if (userId != null) {
            filters.put("customer.id", userId);
        }

        if (firstName != null && !firstName.isBlank()) {
            filters.put("customer.firstName", firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            filters.put("customer.lastName", lastName);
        }

        if (email != null && !email.isBlank()) {
            filters.put("customer.email", email);
        }

        if (phoneNo != null && !phoneNo.isBlank()) {
            filters.put("customer.phoneNo", phoneNo);
        }
        // Trip search params

        if (tripId != null) {
            filters.put("trip.id", tripId);
        }

        if (departureDateTime != null) {
            filters.put("trip.departureDateTime", departureDateTime);
        }

        if (departureLocation != null && !departureLocation.isBlank()) {
            filters.put("trip.departureLocation", departureLocation);
        }

        if (destinationLocation != null && !destinationLocation.isBlank()) {
            filters.put("trip.destinationLocation", destinationLocation);
        }

        if (price != null) {
            filters.put("trip.price", price);
        }

        Specification<CustomerTrip> spec =
                new GenericSearchSpecification<CustomerTrip>().build(filters);

        if (keyword != null && keyword.length() >= 3) {
            Specification<CustomerTrip> keywordSpec =
                    BookingSearchSpecs.keywordSearch(keyword);

            spec = (spec == null)
                    ? keywordSpec
                    : spec.and(keywordSpec);
        }

        Page<CustomerTrip> bookingPage =
                bookingRepository.findAll(spec, pageable);

        return bookingPage.map(BookingMapper::toDTO);
    }
}

