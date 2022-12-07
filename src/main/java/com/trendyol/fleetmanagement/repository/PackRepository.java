package com.trendyol.fleetmanagement.repository;

import com.trendyol.fleetmanagement.model.Pack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PackRepository extends JpaRepository<Pack, UUID> {

    Optional<Pack> findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc(String barcode, Integer desi);

    Set<Pack> findByStateOrderByCreatedAtDesc(Integer state);
}
