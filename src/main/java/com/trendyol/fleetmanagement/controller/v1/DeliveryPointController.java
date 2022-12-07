package com.trendyol.fleetmanagement.controller.v1;

import com.trendyol.fleetmanagement.dto.DeliveryPointDto;
import com.trendyol.fleetmanagement.model.PlaceOfDelivery;
import com.trendyol.fleetmanagement.request.DeliveryPointRequest;
import com.trendyol.fleetmanagement.request.VehicleRequest;
import com.trendyol.fleetmanagement.service.DeliveryPointService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/delivery-point")
public class DeliveryPointController {

    private final DeliveryPointService deliveryPointService;

    public DeliveryPointController(DeliveryPointService deliveryPointService) {
        this.deliveryPointService = deliveryPointService;
    }

    @PostMapping
    @ApiOperation(value = "Create a Delivery Point")
    public ResponseEntity<DeliveryPointDto> createDeliveryPoint(@Valid @RequestBody DeliveryPointRequest deliveryPointRequest){
        return ResponseEntity.ok(deliveryPointService.createDeliveryPoint(deliveryPointRequest));
    }

    @GetMapping(value = "/by-code")
    @ApiOperation(value = "Get a Specific Delivery Point Instance by Code")
    public ResponseEntity<DeliveryPointDto> getDeliveryPointWithThisCode(@RequestParam String code){
        return ResponseEntity.ok(deliveryPointService.getDeliveryPointWithThisCode(code));
    }

    @GetMapping(value = "/by-place-of-delivery")
    @ApiOperation(value = "Get a Specific Delivery Points Instance by Place of Delivery")
    public ResponseEntity<Set<DeliveryPointDto>> getAllDeliveryPointsInThisState(@RequestParam PlaceOfDelivery placeOfDelivery) {
        return ResponseEntity.ok(deliveryPointService.getAllDeliveryPointsInThisState(placeOfDelivery));
    }

    @PostMapping(value = "/distribute-to-delivery-points")
    @ApiOperation(value = "Distribute to Delivery Points")
    public ResponseEntity<Set<DeliveryPointDto>> distributeToDeliveryPoints(@Valid @RequestBody VehicleRequest vehicleRequest) {
        return ResponseEntity.ok(deliveryPointService.distributeToDeliveryPoints(vehicleRequest));
    }

}
