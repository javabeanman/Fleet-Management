package com.trendyol.fleetmanagement.repository;

import com.trendyol.fleetmanagement.model.Sack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SackRepository extends JpaRepository<Sack, UUID> {

    Optional<Sack> findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(String barcode, Integer desi);

    Set<Sack> findByStateOrderByCreatedAtDesc(Integer state);

}
