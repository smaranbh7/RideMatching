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
        log.info("New ride request from rider: {}", request.getRiderId());

        //First saving Ride requested by costumer in DB
        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickUpAddress(request.getPickUpAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimateFare(request));

        Ride savedRide = rideRepository.save(ride);

        //Then publish event to kafka
        //Matching service will consume it and find nearest driver
        RideRequestedEvent event = new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickUpAddress(),
                savedRide.getDropLatitude(),
                savedRide.getDropLongitude(),
                savedRide.getDropAddress()
        );
        kafkaTemplate.send(RIDE_REQUESTED_TOPIC, savedRide.getId(), event);
        log.info("RideRequestedEvent published to Kafka for ride: {}", savedRide.getId());

        //updating status to matching
        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);


        return mapToResponse(savedRide);
    }

    public RideResponse mapToResponse(Ride ride){
        RideResponse response = new RideResponse();
        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());
        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());
        response.setPickUpAddress(ride.getPickUpAddress());
        response.setDropLatitude(ride.getDropLatitude());
        response.setDropLongitude(ride.getDropLongitude());
        response.setDropAddress(ride.getDropAddress());
        response.setStatus(ride.getStatus());
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setActualFare(ride.getActualFare());
        response.setCreatedAt(ride.getCreatedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());

        return response;
    }

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
