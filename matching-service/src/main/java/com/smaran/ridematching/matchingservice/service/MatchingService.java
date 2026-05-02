package com.smaran.ridematching.matchingservice.service;
/*
**Matching Logic:
1. Ask location-service nearby drivers
2. Score each driver based on distance, ratings etc.
3. Pick best score driver
4, Publish ride matched even to Kafka
 */

import com.smaran.ridematching.matchingservice.client.LocationServiceClient;
import com.smaran.ridematching.matchingservice.dto.NearByDriverResponse;
import com.smaran.ridematching.matchingservice.event.RideMatchedEvent;
import com.smaran.ridematching.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC= "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_MILES= 5.0;

    /*
    Called when RideRequestedEvent is consumed / requested by user
     */
    public void matchDriverForRide(RideRequestedEvent event){

        //First, ask location service for nearby drivers
        List<NearByDriverResponse> nearByDrivers = locationServiceClient.getNearByDrivers(
                event.getPickupLatitude(),
                event.getPickupLongitude(),
                DEFAULT_SEARCH_RADIUS_MILES
        );

        if(nearByDrivers.isEmpty()){
            log.warn("No drivers found nearby");
            return;
        }

        //If found, score each driver and pick the best one
        Optional<NearByDriverResponse> bestDriver= findBestDriver(nearByDrivers);

        if(bestDriver.isEmpty()){
            log.warn("Could not find suitable driver for ride");
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        //Publish best RideMatched event to kafka
        RideMatchedEvent rideMatchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInMiles()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), rideMatchedEvent);
        log.info("RideMatched event published");
    }

    /*
    Match scoring algorithm.
    Factors: Distance : 70 %, rating 30% (can add many more)
    score = (1/distance) * distanceWeight + rating * ratingWeight
    add 0.1 in distance to avoid division by 0 if distance is 0
     */
    private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> drivers) {

        double distanceWeight= 0.7;
        double ratingWeight= 0.3;

        return drivers.stream()
                .max(Comparator.comparingDouble(driver ->{
                    double distanceScore = 1.0/(driver.getDistanceInMiles() + 0.1);

                    //Simulated rating between 4.0 and 5.0
                    double simulatedRating = 4.0 + Math.random();

                    return (distanceScore * distanceWeight) + (simulatedRating * ratingWeight);
                }));
    }
}
