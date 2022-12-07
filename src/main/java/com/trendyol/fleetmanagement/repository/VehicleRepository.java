package com.trendyol.fleetmanagement.repository;

import com.trendyol.fleetmanagement.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    boolean existsByPlate(String plate);
    Optional<Vehicle> findByPlate(String plate);

    Set<Vehicle> findByStateOrderByCreatedAtDesc(Integer state);

}
