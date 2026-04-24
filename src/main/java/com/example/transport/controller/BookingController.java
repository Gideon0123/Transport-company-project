package com.example.transport.controller;

import com.example.transport.dto.BookingRequestDTO;
import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.payload.ApiResponse;
import com.example.transport.payload.PagedResponse;
import com.example.transport.dto.UpdateBookingRequestDTO;
import com.example.transport.service.BookingService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(@RequestBody BookingRequestDTO request, HttpServletRequest httpServletRequest) {
        BookingResponseDTO booking = bookingService.createBooking(request);

        return ResponseEntity.ok(
                ApiResponse.<BookingResponseDTO>builder()
                        .success(true)
                        .message("Booking Created successfully")
                        .statusCode(201)
                        .data(booking)
                        .errors(null)
                        .path(httpServletRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Object>> cancelBooking(@PathVariable Long id, HttpServletRequest request) {
        bookingService.cancelBooking(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Booking cancelled successfully")
                        .statusCode(200)
                        .data(null)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBooking(@PathVariable Long id, HttpServletRequest request) {
        BookingResponseDTO booking = bookingService.getBooking(id);

        return ResponseEntity.ok(
                ApiResponse.<BookingResponseDTO>builder()
                        .success(true)
                        .message("Booking Fetched successfully")
                        .statusCode(200)
                        .data(booking)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponseDTO>>> getPagedBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "bookingId") String sortBy, HttpServletRequest request
    ) {
        PagedResponse<BookingResponseDTO> bookings = bookingService.getPagedBookings(page, size, sortBy);
        PagedResponse<BookingResponseDTO> response = PagedResponse.<BookingResponseDTO>builder()
                .content(bookings.getContent())
                .size(bookings.getSize())
                .totalElements(bookings.getTotalElements())
                .totalPages(bookings.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<BookingResponseDTO>>builder()
                        .success(true)
                        .message("Bookings fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) {
        String email = userDetails.getUsername();

        List<BookingResponseDTO> bookings = bookingService.getMyBookings(email);

        return ResponseEntity.ok(
                ApiResponse.<List<BookingResponseDTO>>builder()
                        .success(true)
                        .message("User bookings fetched successfully")
                        .statusCode(200)
                        .data(bookings)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBooking(
            @PathVariable Long bookingId,
            @RequestBody UpdateBookingRequestDTO request,
            HttpServletRequest httpServletRequest
    ) {
        BookingResponseDTO booking = bookingService.updateBooking(bookingId, request);

        return ResponseEntity.ok(
                ApiResponse.<BookingResponseDTO>builder()
                        .success(true)
                        .message("Booking Updated successfully")
                        .statusCode(200)
                        .data(booking)
                        .errors(null)
                        .path(httpServletRequest.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponseDTO>>> searchBookings(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "bookingId") String sortBy,
            HttpServletRequest request
    ) {

        Page<BookingResponseDTO> bookingsPage =
                bookingService.searchBookings(keyword, page, size, sortBy);

        PagedResponse<BookingResponseDTO> response =
                PagedResponse.<BookingResponseDTO>builder()
                        .content(bookingsPage.getContent())
                        .page(bookingsPage.getNumber())
                        .size(bookingsPage.getSize())
                        .totalElements(bookingsPage.getTotalElements())
                        .totalPages(bookingsPage.getTotalPages())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<BookingResponseDTO>>builder()
                        .success(true)
                        .message("Bookings fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
