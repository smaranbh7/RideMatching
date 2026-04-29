package com.smaran.ridemaching.locationservice.service;

import com.smaran.ridemaching.locationservice.dto.DriverLocationRequest;
import com.smaran.ridemaching.locationservice.dto.NearByDriverResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
    }

    public @Nullable List<NearByDriverResponse> findNearByDrivers(double latitude, double longitude, double radius) {
    }

    public void removeDriver(String driverId) {
    }
}
