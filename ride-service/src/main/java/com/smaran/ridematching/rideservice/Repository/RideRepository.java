package com.smaran.ridematching.rideservice.Repository;

import com.smaran.ridematching.rideservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, String> {
    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);
}
