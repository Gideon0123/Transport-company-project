package com.example.transport.service;

import com.example.transport.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BookingProjection {

    Long getBookingId();
    String getEmail();
    Long getTripId();
    String getDepartureLocation();
    String getDestinationLocation();
    LocalDateTime getDepartureDateTime();
    Integer getNumberOfSeats();
    BigDecimal getPrice();
    BigDecimal getTotalPrice();
    BookingStatus getStatus();
}
