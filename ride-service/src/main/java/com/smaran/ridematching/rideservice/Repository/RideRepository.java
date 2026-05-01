package com.smaran.ridematching.rideservice.Repository;

import com.smaran.ridematching.rideservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
}
