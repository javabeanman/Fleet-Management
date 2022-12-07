package com.trendyol.fleetmanagement.dto.converter;

import com.trendyol.fleetmanagement.dto.DeliveryPointDto;
import com.trendyol.fleetmanagement.model.DeliveryPoint;
import com.trendyol.fleetmanagement.model.PlaceOfDelivery;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DeliveryPointConverter {

    private final PackConverter packConverter;

    private final SackConverter sackConverter;

    private final VehicleConverter vehicleConverter;


    public DeliveryPointConverter(PackConverter packConverter, SackConverter sackConverter,@Lazy VehicleConverter vehicleConverter) {
        this.packConverter = packConverter;
        this.sackConverter = sackConverter;
        this.vehicleConverter = vehicleConverter;
    }

    public DeliveryPointDto convert(DeliveryPoint deliveryPoint) {
        return new DeliveryPointDto.Builder()
                .name(deliveryPoint.getName())
                .code(deliveryPoint.getCode())
                .placeOfDelivery(PlaceOfDelivery.Companion.fromInt(deliveryPoint.getPlaceOfDelivery()))
                .build();
    }

    public DeliveryPointDto convertWithAllParam(DeliveryPoint deliveryPoint) {
        return new DeliveryPointDto.Builder()
                .name(deliveryPoint.getName())
                .code(deliveryPoint.getCode())
                .placeOfDelivery(PlaceOfDelivery.Companion.fromInt(deliveryPoint.getPlaceOfDelivery()))
                .sacks(deliveryPoint.getSacks().stream()
                        .map(sackConverter::convert)
                        .collect(Collectors.toSet()))
                .packs(deliveryPoint.getPacks().stream()
                        .map(packConverter::convert)
                        .collect(Collectors.toSet()))
                .vehicles(deliveryPoint.getVehicles().stream()
                        .map(vehicleConverter::convert)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Set<DeliveryPointDto> convert(Set<DeliveryPoint> deliveryPoints) {
        if(deliveryPoints.isEmpty())
            return Collections.emptySet();

        return deliveryPoints.stream()
                .map(this::convert)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<DeliveryPointDto> convertWithAllParam(Set<DeliveryPoint> deliveryPoints) {
        if(deliveryPoints.isEmpty())
            return Collections.emptySet();

        return deliveryPoints.stream()
                .map(this::convertWithAllParam)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
