package com.example.transport.service;

import com.example.transport.dto.BookingRequestDTO;
import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.dto.CreateBookingDTO;
import com.example.transport.payload.PagedResponse;
import com.example.transport.dto.UpdateBookingRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;

public interface BookingService {

    BookingResponseDTO createBooking(CreateBookingDTO request, UserDetails userDetails);
    void cancelBooking(Long id);
    BookingResponseDTO getBooking(Long id);
    PagedResponse<BookingResponseDTO> getPagedBookings(int page, int size, String sortBy);
    Page<BookingResponseDTO> getMyBookings(String email, Pageable pageable);
    BookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request);
    Page<BookingResponseDTO> searchBookings(
        String keyword,
        BigDecimal totalPrice,
        String status,
        Long tripId,
        Pageable pageable);
}
