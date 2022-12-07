package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseServiceTest implements BaseService {

    @Test
    void throwErrorIfNotThisState_IfPackStatusIsEqual_ReturnPack() {
        Pack pack = generatePack(PackState.CREATED);

        Pack result = throwErrorIfNotThisState(pack, PackState.CREATED);

        assertEquals(pack, result);
    }

    @Test
    void throwErrorIfNotThisState_IfPackStatusIsNotEqual_ThrowsException() {
        Pack pack = generatePack(PackState.CREATED);

        FleetManagementException exception = assertThrows(FleetManagementException.class, () -> throwErrorIfNotThisState(pack, PackState.LOADED));

        assertEquals(ErrorMessage.PACK_CANNOT_BE_USED.getMessage(), exception.getMessage());
    }

    @Test
    void throwErrorIfNotThisState_IfSackStatusIsEqual_ReturnSack() {
        Sack sack = generateSack(SackState.CREATED);

        Sack result = throwErrorIfNotThisState(sack, SackState.CREATED);

        assertEquals(sack, result);
    }

    @Test
    void throwErrorIfNotThisState_IfSackStatusIsNotEqual_ThrowsException() {
        Sack sack = generateSack(SackState.CREATED);

        FleetManagementException exception = assertThrows(FleetManagementException.class, () -> throwErrorIfNotThisState(sack, SackState.LOADED));

        assertEquals(ErrorMessage.SACK_CANNOT_BE_USED.getMessage(), exception.getMessage());
    }

    @Test
    void throwErrorIfNotThisState_IfVehicleStatusIsEqual_ReturnVehicle() {
        Vehicle vehicle = generateVehicle(VehicleState.AVAILABLE);

        Vehicle result = throwErrorIfNotThisState(vehicle, VehicleState.AVAILABLE);

        assertEquals(vehicle, result);
    }

    @Test
    void throwErrorIfNotThisState_IfVehicleStatusIsEqual_ThrowsException() {
        Vehicle vehicle = generateVehicle(VehicleState.AVAILABLE);

        FleetManagementException exception = assertThrows(FleetManagementException.class, () -> throwErrorIfNotThisState(vehicle, VehicleState.LOADING));

        assertEquals(ErrorMessage.VEHICLE_CANNOT_BE_USED.getMessage(), exception.getMessage());
    }

    private Pack generatePack(PackState state){
        return new Pack.Builder()
                .id(UUID.randomUUID())
                .state(state.getValue())
                .barcode("P9988000121")
                .desi(1)
                .build();
    }

    private Sack generateSack(SackState state){
        return new Sack.Builder()
                .id(UUID.randomUUID())
                .state(state.getValue())
                .sackBarcode("C725791")
                .desi(40)
                .freeDesi(40)
                .size(0)
                .build();
    }

    private Vehicle generateVehicle(VehicleState state){
        return new Vehicle.Builder()
                .id(UUID.randomUUID())
                .state(state.getValue())
                .plate("34TL311")
                .build();
    }
}