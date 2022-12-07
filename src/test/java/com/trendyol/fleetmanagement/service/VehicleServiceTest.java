package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.TestSupport;
import com.trendyol.fleetmanagement.dto.VehicleDto;
import com.trendyol.fleetmanagement.dto.converter.VehicleConverter;
import com.trendyol.fleetmanagement.model.PlaceOfDelivery;
import com.trendyol.fleetmanagement.model.Vehicle;
import com.trendyol.fleetmanagement.model.VehicleState;
import com.trendyol.fleetmanagement.repository.VehicleRepository;
import com.trendyol.fleetmanagement.request.VehicleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class VehicleServiceTest extends TestSupport {

    private VehicleConverter vehicleConverter;

    private VehicleRepository vehicleRepository;

    private DeliveryPointService deliveryPointService;

    private SackService sackService;

    private PackService packService;

    private VehicleService vehicleService;

    @BeforeEach
    public void setUp() {
        vehicleConverter = mock(VehicleConverter.class);
        vehicleRepository = mock(VehicleRepository.class);
        deliveryPointService = mock(DeliveryPointService.class);
        sackService = mock(SackService.class);
        packService = mock(PackService.class);
        vehicleService = new VehicleService(vehicleRepository, vehicleConverter, deliveryPointService, sackService, packService);
    }

    @Test
    void createVehicle_IfSaveIsSuccess_ReturnVehicleDto(){
        VehicleRequest request = new VehicleRequest("34TL311");
        Vehicle savedVehicle = new Vehicle(UUID.randomUUID(),VehicleState.AVAILABLE.getValue(), "34TL311", Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.EMPTY_SET);
        VehicleDto vehicleDto = new VehicleDto("34TL311",VehicleState.AVAILABLE, Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.EMPTY_SET);

        when(vehicleRepository.existsByPlate("34TL311")).thenReturn(Boolean.FALSE);
        when(vehicleRepository.save(Mockito.any(Vehicle.class))).thenReturn(savedVehicle);
        when(vehicleConverter.convert(savedVehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(request);

        assertEquals(vehicleDto, result);

        verify(vehicleRepository).existsByPlate("34TL311");
        verify(vehicleRepository).save(Mockito.any(Vehicle.class));
        verify(vehicleConverter).convert(savedVehicle);
    }

    @Test
    void getVehicleWithThisPlate_IsItSuccess_ReturnVehicleDto() {
        Vehicle getVehicle = generateVehicles(1, VehicleState.AVAILABLE, PlaceOfDelivery.BRANCH).iterator().next();
        VehicleDto vehicleDto = generateVehicleDtos(Collections.singleton(getVehicle)).iterator().next();

        when(vehicleRepository.findByPlate("34TL311")).thenReturn(Optional.of(getVehicle));
        when(vehicleConverter.convertWithAllParam(getVehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleWithThisPlate("34TL311");

        assertEquals(vehicleDto, result);

        verify(vehicleRepository).findByPlate("34TL311");
        verify(vehicleConverter).convertWithAllParam(getVehicle);
    }

    @Test
    void getAllVehiclesInThisState_IsItSuccess_ReturnVehicleDtos() {
        Set<Vehicle> vehicles = generateVehicles(5, VehicleState.LOADING, PlaceOfDelivery.DISTRIBUTION_CENTER);
        Set<VehicleDto> vehicleDtos = generateVehicleDtos(vehicles);

        when(vehicleRepository.findByStateOrderByCreatedAtDesc(VehicleState.LOADING.getValue())).thenReturn(vehicles);
        when(vehicleConverter.convertWithAllParam(vehicles)).thenReturn(vehicleDtos);

        Set<VehicleDto> result = vehicleService.getAllVehiclesInThisState(VehicleState.LOADING);

        assertEquals(vehicleDtos, result);
        verify(vehicleRepository).findByStateOrderByCreatedAtDesc(VehicleState.LOADING.getValue());
        verify(vehicleConverter).convertWithAllParam(vehicles);

    }
}