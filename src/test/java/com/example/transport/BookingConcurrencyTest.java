package com.example.transport;

import com.example.transport.dto.CreateBookingDTO;
import com.example.transport.model.Trip;
import com.example.transport.repository.CustomerTripRepository;
import com.example.transport.repository.TripRepository;
import com.example.transport.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private CustomerTripRepository bookingRepository;

    private static final Long TRIP_ID = 2L;

    @BeforeEach
    void setup() {

        Trip trip = tripRepository.findById(TRIP_ID)
                .orElseThrow();

        trip.setTotalNoOfPassengers(5);

        trip.setBookedSeats(0);

        tripRepository.save(trip);
    }

    @Test
    void testConcurrentBookings() throws InterruptedException {

        int numberOfThreads = 20;

        ExecutorService executorService =
                Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch latch =
                new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {

            executorService.submit(() -> {

                try {

                    CreateBookingDTO request =
                            new CreateBookingDTO();

                    request.setTripId(TRIP_ID);

                    request.setNumberOfSeats(1);

                    UserDetails userDetails =
                            User.withUsername("joulerVanessa@gmail.com")
                                    .password("password")
                                    .authorities("ROLE_USER")
                                    .build();

                    bookingService.createBooking(
                            request,
                            userDetails
                    );

                    System.out.println(
                            "BOOKING SUCCESS"
                    );

                } catch (Exception e) {

                    System.out.println(
                            "BOOKING FAILED: "
                                    + e.getMessage()
                    );

                } finally {

                    latch.countDown();
                }
            });
        }

        latch.await();

        Trip updatedTrip = tripRepository.findById(TRIP_ID)
                .orElseThrow();

        System.out.println(
                "FINAL BOOKED SEATS = "
                        + updatedTrip.getBookedSeats()
        );

        assertTrue(
                updatedTrip.getBookedSeats() <=
                        updatedTrip.getTotalNoOfPassengers()
        );
    }
}