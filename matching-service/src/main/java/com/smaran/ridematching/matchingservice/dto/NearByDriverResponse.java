package com.smaran.ridematching.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//This response is received from location-service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInMiles;
}

