package com.example.transport.controller;

import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.dto.CreateBookingDTO;
import com.example.transport.payload.ApiResponse;
import com.example.transport.payload.PagedResponse;
import com.example.transport.dto.UpdateBookingRequestDTO;
import com.example.transport.service.BookingService;
import com.example.transport.util.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody CreateBookingDTO request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpServletRequest
    ) {

        BookingResponseDTO booking =
                bookingService.createBooking(request, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BookingResponseDTO>builder()
                        .success(true)
                        .message("Booking created successfully")
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
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request
    ) {
        String email = userDetails.getUsername();

        Page<BookingResponseDTO> bookings =
                bookingService.getMyBookings(email, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<BookingResponseDTO>>builder()
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

    @PutMapping("/{bookingId}")
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
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> searchBookings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal totalPrice,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long tripId,

            @PageableDefault(size = 5, sort = "totalPrice")
            Pageable pageable,
            HttpServletRequest request
    ) {

        Page<BookingResponseDTO> bookingsPage =
                bookingService.searchBookings(
                        keyword,
                        totalPrice,
                        status,
                        tripId,
                        pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<BookingResponseDTO>>builder()
                        .success(true)
                        .message("Bookings fetched successfully")
                        .statusCode(200)
                        .data(bookingsPage)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
