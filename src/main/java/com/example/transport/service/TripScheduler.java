package com.example.transport.service;

import com.example.transport.enums.TripStatus;
import com.example.transport.model.Trip;
import com.example.transport.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripScheduler {

    private final TripRepository tripRepository;

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void updateTripStatuses() {

        List<Trip> trips = tripRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Trip trip : trips) {

            // PENDING → ONGOING
            if (trip.getStatus() == TripStatus.PENDING &&
                    trip.getDepartureDateTime().isBefore(now)) {

                trip.setStatus(TripStatus.ONGOING);
            }

            // ONGOING → COMPLETED (after 5 hours for example)
            if (trip.getStatus() == TripStatus.ONGOING &&
                    trip.getDepartureDateTime().plusHours(5).isBefore(now)) {

                trip.setStatus(TripStatus.COMPLETED);
            }
        }

        tripRepository.saveAll(trips);
    }
}