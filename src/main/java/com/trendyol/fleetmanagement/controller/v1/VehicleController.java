package com.trendyol.fleetmanagement.controller.v1;

import com.trendyol.fleetmanagement.dto.VehicleDto;
import com.trendyol.fleetmanagement.model.VehicleState;
import com.trendyol.fleetmanagement.request.DeliveryPointToVehicleRequest;
import com.trendyol.fleetmanagement.request.PackAndSackToVehicleRequest;
import com.trendyol.fleetmanagement.request.VehicleRequest;
import com.trendyol.fleetmanagement.service.VehicleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ApiOperation(value = "Create a Vehicle")
    public ResponseEntity<VehicleDto> createVehicle(@Valid @RequestBody VehicleRequest vehicleRequest){
        return ResponseEntity.ok(vehicleService.createVehicle(vehicleRequest));
    }

    @GetMapping(value = "/by-plate")
    @ApiOperation(value = "Get a Specific Vehicle Instance by Plate")
    public ResponseEntity<VehicleDto> getVehicleWithThisPlate(@RequestParam String plate){
        return ResponseEntity.ok(vehicleService.getVehicleWithThisPlate(plate));
    }

    @GetMapping(value = "/by-state")
    @ApiOperation(value = "Get a Specific Vehicles Instance by State")
    public ResponseEntity<Set<VehicleDto>> getAllVehiclesInThisState(@RequestParam VehicleState state){
        return ResponseEntity.ok(vehicleService.getAllVehiclesInThisState(state));
    }

    @PostMapping(value = "/add-delivery-points-to-vehicle")
    @ApiOperation(value = "Add Delivery Points To Vehicle")
    public ResponseEntity<VehicleDto> addDeliveryPointsToVehicle(@Valid @RequestBody DeliveryPointToVehicleRequest deliveryPointToVehicleRequest){
        return ResponseEntity.ok(vehicleService.addDeliveryPointToVehicle(deliveryPointToVehicleRequest));
    }

    @PostMapping(value = "/remove-delivery-points-to-vehicle")
    @ApiOperation(value = "Remove Delivery Points To Vehicle")
    public ResponseEntity<VehicleDto> removeDeliveryPointToVehicle(@Valid @RequestBody DeliveryPointToVehicleRequest deliveryPointToVehicleRequest){
        return ResponseEntity.ok(vehicleService.removeDeliveryPointToVehicle(deliveryPointToVehicleRequest));
    }


    @PostMapping(value = "/add-packs-and-sacks-to-vehicle")
    @ApiOperation("Add Packs and Sacks To Vehicle")
    public ResponseEntity<VehicleDto> addPacksAndSacksToVehicle(@Valid @RequestBody PackAndSackToVehicleRequest packAndSackToVehicleRequest){
        return ResponseEntity.ok(vehicleService.addPacksAndSacksToVehicle(packAndSackToVehicleRequest));
    }

    @PostMapping(value = "/remove-packs-and-sacks-to-vehicle")
    @ApiOperation("Remove Packs and Sacks To Vehicle")
    public ResponseEntity<VehicleDto> removePacksAndSacksToVehicle(@Valid @RequestBody PackAndSackToVehicleRequest packAndSackToVehicleRequest){
        return ResponseEntity.ok(vehicleService.removePacksAndSacksToVehicle(packAndSackToVehicleRequest));
    }

    @PostMapping(value = "/complete-vehicle-loading")
    @ApiOperation(value = "Complete Vehicle Loading")
    public ResponseEntity<VehicleDto> completeVehicleLoading(@Valid @RequestBody VehicleRequest vehicleRequest){
        return ResponseEntity.ok(vehicleService.completeVehicleLoading(vehicleRequest));
    }


}
