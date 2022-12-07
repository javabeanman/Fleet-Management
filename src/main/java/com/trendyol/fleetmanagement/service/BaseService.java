package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.*;

import javax.validation.constraints.NotNull;

public interface BaseService {

    @NotNull
    default Pack throwErrorIfNotThisState(Pack pack, PackState packState) {
        if (packState.getValue() != pack.getState())
            throw new FleetManagementException(ErrorMessage.PACK_CANNOT_BE_USED, pack.getBarcode(), pack.getDesi(), packState.name());

        return pack;
    }

    @NotNull
    default Sack throwErrorIfNotThisState(Sack sack, SackState sackState) {
        if (sackState.getValue() != sack.getState())
            throw new FleetManagementException(ErrorMessage.SACK_CANNOT_BE_USED, sack.getSackBarcode(), sack.getDesi(), sackState.name());

        return sack;
    }

    @NotNull
    default Vehicle throwErrorIfNotThisState(Vehicle vehicle, VehicleState vehicleState) {
        if (vehicleState.getValue() != vehicle.getState())
            throw new FleetManagementException(ErrorMessage.VEHICLE_CANNOT_BE_USED, vehicle.getPlate(), vehicleState.name());

        return vehicle;
    }



}
