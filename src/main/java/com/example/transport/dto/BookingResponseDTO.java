package com.example.transport.dto;

import com.example.transport.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class BookingResponseDTO {

    private Long bookingId;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;
    private BookingStatus status;

    private TripSimpleDTO trip;
    private UserSimpleDTO customer;

    public BookingResponseDTO(
            Long bookingId,
            Integer numberOfSeats,
            BigDecimal totalPrice,
            BookingStatus status,
            TripSimpleDTO trip,
            UserSimpleDTO customer
    ) {
        this.bookingId = bookingId;
        this.numberOfSeats = numberOfSeats;
        this.totalPrice = totalPrice;
        this.status = status;
        this.trip = trip;
        this.customer = customer;
    }
}