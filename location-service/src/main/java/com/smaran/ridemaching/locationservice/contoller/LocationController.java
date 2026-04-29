package com.smaran.ridemaching.locationservice.contoller;

import com.smaran.ridemaching.locationservice.dto.DriverLocationRequest;
import com.smaran.ridemaching.locationservice.dto.NearByDriverResponse;
import com.smaran.ridemaching.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // driver calls this endpoint to post their current location in every 3 seconds
    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(
            @RequestBody DriverLocationRequest driverLocationRequest){
        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Driver Location updated");
    }

    //Matching service calls this endpoint to match with nearest driver to the user
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam (defaultValue = "5.0") double radius
    ){
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude, longitude, radius));
    }

    // Removing driver when they go offline
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId){
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }
}
