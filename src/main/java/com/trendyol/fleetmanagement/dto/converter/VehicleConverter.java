package com.trendyol.fleetmanagement.dto.converter;

import com.trendyol.fleetmanagement.dto.VehicleDto;
import com.trendyol.fleetmanagement.model.Vehicle;
import com.trendyol.fleetmanagement.model.VehicleState;
import com.trendyol.fleetmanagement.request.VehicleRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleConverter {

    private final PackConverter packConverter;

    private final SackConverter sackConverter;

    private final DeliveryPointConverter deliveryPointConverter;


    public VehicleConverter(PackConverter packConverter, SackConverter sackConverter, @Lazy DeliveryPointConverter deliveryPointConverter) {
        this.packConverter = packConverter;
        this.sackConverter = sackConverter;
        this.deliveryPointConverter = deliveryPointConverter;
    }

    public VehicleDto convert(Vehicle vehicle) {
        return new VehicleDto.Builder()
                .state(VehicleState.Companion.fromInt(vehicle.getState()))
                .plate(vehicle.getPlate())
                .build();
    }

    public VehicleDto convertWithAllParam(Vehicle vehicle) {
        return new VehicleDto.Builder()
                .state(VehicleState.Companion.fromInt(vehicle.getState()))
                .plate(vehicle.getPlate())
                .sacks(vehicle.getSacks().stream()
                        .map(sackConverter::convert)
                        .collect(Collectors.toSet()))
                .packs(vehicle.getPacks().stream()
                        .map(packConverter::convert)
                        .collect(Collectors.toSet()))
                .deliveryPoints(vehicle.getDeliveryPoints().stream()
                        .map(deliveryPointConverter::convert)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Set<VehicleDto> convert(Set<Vehicle> vehicles) {
        if(vehicles.isEmpty())
            return Collections.emptySet();

        return vehicles.stream()
                .map(this::convert)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<VehicleDto> convertWithAllParam(Set<Vehicle> vehicles) {
        if(vehicles.isEmpty())
            return Collections.emptySet();

        return vehicles.stream()
                .map(this::convertWithAllParam)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
