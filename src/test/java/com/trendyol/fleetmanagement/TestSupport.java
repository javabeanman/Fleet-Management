package com.trendyol.fleetmanagement;

import com.trendyol.fleetmanagement.dto.DeliveryPointDto;
import com.trendyol.fleetmanagement.dto.PackDto;
import com.trendyol.fleetmanagement.dto.SackDto;
import com.trendyol.fleetmanagement.dto.VehicleDto;
import com.trendyol.fleetmanagement.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestSupport {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static Set<Pack> generatePacks(int size, PackState packState) {
        return IntStream.range(0, size).mapToObj(i ->
                new Pack.Builder()
                        .id(UUID.randomUUID())
                        .state(packState.getValue())
                        .barcode("P998800012" + i)
                        .desi(1)
                        .build()).collect(Collectors.toSet());
    }

    public static Set<PackDto> generatePackDtos(Set<Pack> packs){
        return packs.stream()
                .map(pack -> new PackDto.Builder()
                        .createdAt(LocalDateTime.now().format(dateTimeFormatter))
                        .state(PackState.Companion.fromInt(pack.getState()))
                        .barcode(pack.getBarcode())
                        .desi(pack.getDesi())
                        .build())
                .collect(Collectors.toSet());
    }

    public static Set<Sack> generateSacks(int size, SackState sackState) {
        return IntStream.range(0, size).mapToObj(i ->
                        new Sack.Builder()
                                .id(UUID.randomUUID())
                                .state(sackState.getValue())
                                .sackBarcode("C72579" + i)
                                .desi(40)
                                .freeDesi(40)
                                .packs(Collections.singleton(new Pack.Builder()
                                        .id(UUID.randomUUID())
                                        .state(PackState.LOADED_INTO_SACK.getValue())
                                        .barcode("P998800012" + i)
                                        .desi(1)
                                        .build()))
                                .build())
                .collect(Collectors.toSet());
    }

    public static Set<SackDto> generateSackDtos(Set<Sack> sacks){
        return sacks.stream()
                .map(sack -> new SackDto.Builder()
                        .createdAt(LocalDateTime.now().format(dateTimeFormatter))
                        .state(SackState.Companion.fromInt(sack.getState()))
                        .sackBarcode(sack.getSackBarcode())
                        .desi(sack.getDesi())
                        .freeDesi(sack.getFreeDesi())
                        .packs(sack.getPacks().stream().map(pack -> new PackDto.Builder()
                                .state(PackState.Companion.fromInt(pack.getState()))
                                .barcode(pack.getBarcode())
                                .desi(pack.getDesi())
                                .build()).collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());
    }

    public static Set<Vehicle> generateVehicles(int size, VehicleState vehicleState, PlaceOfDelivery placeOfDelivery){
        return IntStream.range(0,size).mapToObj(i ->
                new Vehicle.Builder()
                        .id(UUID.randomUUID())
                        .state(vehicleState.getValue())
                        .plate("34TL31" + i)
                        .packs(Collections.singleton(new Pack.Builder()
                                .id(UUID.randomUUID())
                                .state(PackState.LOADED.getValue())
                                .barcode("P998800012" + i)
                                .desi(1)
                                .build()))
                        .sacks(Collections.singleton(new Sack.Builder()
                                .id(UUID.randomUUID())
                                .state(SackState.LOADED.getValue())
                                .sackBarcode("C72579" + i)
                                .desi(40)
                                .freeDesi(40)
                                .packs(Collections.singleton(new Pack.Builder()
                                        .id(UUID.randomUUID())
                                        .state(PackState.LOADED_INTO_SACK.getValue())
                                        .barcode("P998800011" + i)
                                        .desi(1)
                                        .build()))
                                .build()))
                        .deliveryPoints(Collections.singleton(new DeliveryPoint.Builder()
                                .id(UUID.randomUUID())
                                .placeOfDelivery(placeOfDelivery.getValue())
                                .code("DP23000" + i)
                                .name("Example" + i)
                                .build()))
                        .build())
                .collect(Collectors.toSet());
    }

    public static Set<VehicleDto> generateVehicleDtos(Set<Vehicle> vehicles){
        return vehicles.stream()
                .map(vehicle -> new VehicleDto.Builder()
                        .state(VehicleState.Companion.fromInt(vehicle.getState()))
                        .plate(vehicle.getPlate())
                        .packs(vehicle.getPacks().stream().map(pack -> new PackDto.Builder()
                                .state(PackState.Companion.fromInt(pack.getState()))
                                .barcode(pack.getBarcode())
                                .desi(pack.getDesi())
                                .build()).collect(Collectors.toSet()))
                        .sacks(vehicle.getSacks().stream().map(sack -> new SackDto.Builder()
                                .state(SackState.Companion.fromInt(sack.getState()))
                                .sackBarcode(sack.getSackBarcode())
                                .desi(sack.getDesi())
                                .freeDesi(sack.getFreeDesi())
                                .packs(sack.getPacks().stream().map(pack -> new PackDto.Builder()
                                        .state(PackState.Companion.fromInt(pack.getState()))
                                        .barcode(pack.getBarcode())
                                        .desi(pack.getDesi())
                                        .build()).collect(Collectors.toSet()))
                                .build()).collect(Collectors.toSet()))
                        .deliveryPoints(vehicle.getDeliveryPoints().stream().map(deliveryPoint -> new DeliveryPointDto.Builder()
                                .placeOfDelivery(PlaceOfDelivery.Companion.fromInt(deliveryPoint.getPlaceOfDelivery()))
                                .code(deliveryPoint.getCode())
                                .name(deliveryPoint.getName())
                                .build()).collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());
    }

    public static Set<DeliveryPoint> generateDeliveryPoints(int size, PlaceOfDelivery placeOfDelivery, VehicleState vehicleState){
        return IntStream.range(0,size).mapToObj(i ->
                new DeliveryPoint.Builder()
                        .id(UUID.randomUUID())
                        .placeOfDelivery(placeOfDelivery.getValue())
                        .code("DP23000" + i)
                        .name("Example" + i)
                        .packs(Collections.singleton(new Pack.Builder()
                                .id(UUID.randomUUID())
                                .state(PackState.UNLOADED.getValue())
                                .barcode("P998800012" + i)
                                .desi(1)
                                .build()))
                        .sacks(Collections.singleton(new Sack.Builder()
                                .id(UUID.randomUUID())
                                .state(SackState.UNLOADED.getValue())
                                .sackBarcode("C72579" + i)
                                .desi(40)
                                .freeDesi(40)
                                .packs(Collections.singleton(new Pack.Builder()
                                        .id(UUID.randomUUID())
                                        .state(PackState.LOADED_INTO_SACK.getValue())
                                        .barcode("P998800011" + i)
                                        .desi(1)
                                        .build()))
                                .build()))
                        .vehicles(Collections.singleton(new Vehicle.Builder()
                                .id(UUID.randomUUID())
                                .state(vehicleState.getValue())
                                .plate("34TL31" + i)
                                .build()))
                        .build())

                .collect(Collectors.toSet());
    }

    public static Set<DeliveryPointDto> generateDeliveryPointDtos(Set<DeliveryPoint> deliveryPoints){
        return deliveryPoints.stream()
                .map(deliveryPoint -> new DeliveryPointDto.Builder()
                        .placeOfDelivery(PlaceOfDelivery.Companion.fromInt(deliveryPoint.getPlaceOfDelivery()))
                        .code(deliveryPoint.getCode())
                        .name(deliveryPoint.getName())
                        .packs(deliveryPoint.getPacks().stream().map(pack -> new PackDto.Builder()
                                .state(PackState.Companion.fromInt(pack.getState()))
                                .barcode(pack.getBarcode())
                                .desi(pack.getDesi())
                                .build()).collect(Collectors.toSet()))
                        .sacks(deliveryPoint.getSacks().stream().map(sack -> new SackDto.Builder()
                                .state(SackState.Companion.fromInt(sack.getState()))
                                .sackBarcode(sack.getSackBarcode())
                                .desi(sack.getDesi())
                                .freeDesi(sack.getFreeDesi())
                                .packs(sack.getPacks().stream().map(pack -> new PackDto.Builder()
                                        .state(PackState.Companion.fromInt(pack.getState()))
                                        .barcode(pack.getBarcode())
                                        .desi(pack.getDesi())
                                        .build()).collect(Collectors.toSet()))
                                .build()).collect(Collectors.toSet()))
                        .vehicles(deliveryPoint.getVehicles().stream().map(vehicle -> new VehicleDto.Builder()
                                .state(VehicleState.Companion.fromInt(vehicle.getState()))
                                .plate(vehicle.getPlate())
                                .build()).collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toSet());
    }
}
