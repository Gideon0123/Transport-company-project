package com.example.transport.dto;

import com.example.transport.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class BookingResponseDTO {

    private UserSimpleDTO customer;

    private Long bookingId;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;
    private BookingStatus status;

    private TripSimpleDTO trip;

    public BookingResponseDTO(
            UserSimpleDTO customer,
            Long bookingId,
            Integer numberOfSeats,
            BigDecimal totalPrice,
            BookingStatus status,
            TripSimpleDTO trip
    ) {
        this.customer = customer;
        this.bookingId = bookingId;
        this.numberOfSeats = numberOfSeats;
        this.totalPrice = totalPrice;
        this.status = status;
        this.trip = trip;
    }
}