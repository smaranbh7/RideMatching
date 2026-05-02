package com.smaran.ridematching.rideservice.service;

import com.smaran.ridematching.rideservice.Repository.RideRepository;
import com.smaran.ridematching.rideservice.dto.RideRequest;
import com.smaran.ridematching.rideservice.dto.RideResponse;
import com.smaran.ridematching.rideservice.event.RideRequestedEvent;
import com.smaran.ridematching.rideservice.model.Ride;
import com.smaran.ridematching.rideservice.model.RideStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    //Called by matching service when a driver is found and updates the ride status to ACCEPTED and assigns
    //driver id
    public void updateRideWithDriver(String rideID, String driverId){
        Ride ride= rideRepository.findById(rideID)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);

    }

    public RideResponse getRideById(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        return mapToResponse(ride);
    }

    public  List<RideResponse> getRidesByRider(String riderId) {
        return rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RideResponse startRide(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if(ride.getStatus()!=RideStatus.ACCEPTED){
            throw new RuntimeException("Ride cannot be started. Current status : " + ride.getStatus());
        }

        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);

        return mapToResponse(ride);
    }

    public  RideResponse completeRide(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if(ride.getStatus()!=RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride cannot be completed. Current status : " + ride.getStatus());
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        rideRepository.save(ride);
        ride.setActualFare(ride.getEstimatedFare());
        return mapToResponse(ride);
    }

    public  RideResponse cancelRide(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
        return mapToResponse(ride);
    }

    private double calculateEstimateFare(RideRequest request) {
        //Calculate distance with Haversine distance calculation
        double lat1=Math.toRadians(request.getPickupLatitude());
        double lat2=Math.toRadians(request.getDropLatitude());

        double lon1=Math.toRadians(request.getPickupLongitude());
        double lon2=Math.toRadians(request.getDropLongitude());

        double latDifference = lat2 - lat1;
        double lonDifference = lon2 - lon1;

        double intermediate = Math.pow(Math.sin(latDifference/2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(lonDifference/2),2);

        double angleBetweenTwoPoints = 2 * Math.asin(Math.sqrt(intermediate));

        double distanceMiles = 3959 * angleBetweenTwoPoints;

        //5 dollars base plus 1.5/miles
        double fair = 5 + (distanceMiles * 1.5);
        return Math.round(fair * 100.0) / 100.0;
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
}
