package com.smaran.ridematching.rideservice.service;

import com.smaran.ridematching.rideservice.Repository.RideRepository;
import com.smaran.ridematching.rideservice.dto.RideRequest;
import com.smaran.ridematching.rideservice.dto.RideResponse;
import com.smaran.ridematching.rideservice.event.RideRequestedEvent;
import com.smaran.ridematching.rideservice.model.Ride;
import com.smaran.ridematching.rideservice.model.RideStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;
    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";

    public RideResponse requestRide(RideRequest request) {

    public Object getRideById(String rideId) {
    }

    public @Nullable List<RideResponse> getRiderByRider(String riderId) {
    }

    public @Nullable RideResponse startRide(String rideId) {
    }

    public @Nullable RideResponse completeRide(String rideId) {
    }

    public @Nullable RideResponse cancelRide(String rideId) {
    }
}
