package com.trendyol.fleetmanagement.repository;

import com.trendyol.fleetmanagement.model.DeliveryPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DeliveryPointRepository extends JpaRepository<DeliveryPoint, UUID> {

    boolean existsByCode(String code);
    Optional<DeliveryPoint> findByCode(String code);

    Set<DeliveryPoint> findByPlaceOfDeliveryOrderByCreatedAtDesc(Integer placeOfDelivery);

}