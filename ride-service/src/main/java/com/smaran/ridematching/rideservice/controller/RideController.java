package com.smaran.ridematching.rideservice.controller;

import com.smaran.ridematching.rideservice.dto.RideRequest;
import com.smaran.ridematching.rideservice.dto.RideResponse;
import com.smaran.ridematching.rideservice.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    //rider side api calls
    //Post call form rider to request ride
    @PostMapping("/request")
    public ResponseEntity<RideResponse> requestRide(
            @Valid @RequestBody RideRequest rideRequest
            ){
        log.info("Ride request received from rider {}", rideRequest.getRiderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.requestRide(rideRequest));
    }


    @GetMapping("/{rideId}")
    public RequestEntity<RideResponse> getRideById(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getRideByRider(
            @PathVariable String riderId){
        return ResponseEntity.ok(rideService.getRiderByRider(riderId));
    }

    //driver side api calls
    //Driver starting the ride
    @PutMapping("/{rideId}/start")
    public  ResponseEntity<RideResponse> startRide(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.completeRide(rideId));

    }

    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable String rideId
    ){
        return ResponseEntity.ok(rideService.cancelRide(rideId));

    }
}


