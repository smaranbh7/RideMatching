package com.smaran.ridematching.matchingservice.service;

import com.smaran.ridematching.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
//Listens to ride.requested kafka topic and is triggered every time ride service publishes new ride request
public class RideEventConsumer {

    private final MatchingService matchingService;

    @KafkaListener(
            topics = "ride.requested",
            groupId = "matching-service-group"
    )
    public void consumeRideRequestedEvent (RideRequestedEvent event){
        try{
            matchingService.matchDriverForRide(event);
        }catch (Exception e){
            log.error("Error processing ride request: {} - {}", event.getRideId(), e.getMessage());
        }
    }

}
