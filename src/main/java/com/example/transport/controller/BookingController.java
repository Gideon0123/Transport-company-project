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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public ResponseEntity<ApiResponse<Object>> cancelBooking(
            @PathVariable Long id,
            HttpServletRequest request) {
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
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBooking(
            @PathVariable Long id,
            HttpServletRequest request) {
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "bookingId") String sortBy,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<BookingResponseDTO> bookings = bookingService.getPagedBookings(adjustedPage, size, sortBy);
        PagedResponse<BookingResponseDTO> response = PagedResponse.<BookingResponseDTO>builder()
                .content(bookings.getContent())
                .size(bookings.getSize())
                .page(bookings.getPage())
                .first(bookings.isFirst())
                .last(bookings.isLast())
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
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponseDTO>>> getMyBookings(

            @AuthenticationPrincipal UserDetails userDetails,

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) BigDecimal totalPrice,
            @RequestParam(required = false) String status,

            @RequestParam(required = false) Long tripId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDateTime,

            @RequestParam(required = false) String departureLocation,
            @RequestParam(required = false) String destinationLocation,
            @RequestParam(required = false) BigDecimal price,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "bookingId") String sortBy,

            HttpServletRequest request
    ) {

        String email = userDetails.getUsername();

        int adjustedPage = Math.max(page - 1, 0);

        Pageable pageable = PageRequest.of(
                adjustedPage,
                size,
                Sort.by(sortBy)
        );

        Page<BookingResponseDTO> bookings =
                bookingService.getMyBookings(
                        email,

                        keyword,
                        bookingId,
                        totalPrice,
                        status,

                        tripId,
                        departureDateTime,
                        departureLocation,
                        destinationLocation,
                        price,

                        pageable
                );

        PagedResponse<BookingResponseDTO> response =
                PagedResponse.<BookingResponseDTO>builder()
                        .content(bookings.getContent())
                        .page(bookings.getNumber() + 1)
                        .size(bookings.getSize())
                        .totalElements(bookings.getTotalElements())
                        .totalPages(bookings.getTotalPages())
                        .first(bookings.isFirst())
                        .last(bookings.isLast())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<BookingResponseDTO>>builder()
                        .success(true)
                        .message("User bookings fetched successfully")
                        .statusCode(200)
                        .data(response)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdateBookingRequestDTO request,
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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponseDTO>>> searchBookings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) BigDecimal totalPrice,
            @RequestParam(required = false) String status,

            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNo,

            @RequestParam(required = false) Long tripId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDateTime,
            @RequestParam(required = false) String departureLocation,
            @RequestParam(required = false) String destinationLocation,
            @RequestParam(required = false) BigDecimal price,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "bookingId") String sortBy,
            HttpServletRequest request
    ) {

        //Convert to Spring format (0-based)
        int adjustedPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(sortBy));

        Page<BookingResponseDTO> bookingsPage =
                bookingService.searchBookings(
                        keyword,
                        bookingId,
                        totalPrice,
                        status,

                        userId,
                        firstName,
                        lastName,
                        email,
                        phoneNo,

                        tripId,
                        departureDateTime,
                        departureLocation,
                        destinationLocation,
                        price,
                        pageable
                );

        PagedResponse<BookingResponseDTO> response = PagedResponse.<BookingResponseDTO>builder()
                .content(bookingsPage.getContent())
                .page(bookingsPage.getNumber() + 1)
                .size(bookingsPage.getSize())
                .totalElements(bookingsPage.getTotalElements())
                .totalPages(bookingsPage.getTotalPages())
                .first(bookingsPage.isFirst())
                .last(bookingsPage.isLast())
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
