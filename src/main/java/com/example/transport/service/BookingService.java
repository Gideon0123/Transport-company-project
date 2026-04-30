package com.example.transport.service;

import com.example.transport.dto.BookingRequestDTO;
import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.payload.PagedResponse;
import com.example.transport.dto.UpdateBookingRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO request);
    void cancelBooking(Long id);
    BookingResponseDTO getBooking(Long id);
    PagedResponse<BookingResponseDTO> getPagedBookings(int page, int size, String sortBy);
    List<BookingResponseDTO> getMyBookings(String email);
    BookingResponseDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request);
    Page<BookingResponseDTO> searchBookings(
        String keyword,
        BigDecimal totalPrice,
        String status,
        Long tripId,
        Pageable pageable);
}
