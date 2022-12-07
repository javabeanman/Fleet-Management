package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.TestSupport;
import com.trendyol.fleetmanagement.dto.DeliveryPointDto;
import com.trendyol.fleetmanagement.dto.converter.DeliveryPointConverter;
import com.trendyol.fleetmanagement.model.DeliveryPoint;
import com.trendyol.fleetmanagement.model.PlaceOfDelivery;
import com.trendyol.fleetmanagement.model.VehicleState;
import com.trendyol.fleetmanagement.repository.DeliveryPointRepository;
import com.trendyol.fleetmanagement.request.DeliveryPointRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeliveryPointServiceTest extends TestSupport {

    private DeliveryPointConverter deliveryPointConverter;

    private DeliveryPointRepository deliveryPointRepository;

    private DeliveryPointService deliveryPointService;

    private VehicleService vehicleService;

    private SackService sackService;

    private PackService packService;



    @BeforeEach
    public void setUp() {
        deliveryPointConverter = mock(DeliveryPointConverter.class);
        deliveryPointRepository = mock(DeliveryPointRepository.class);
        vehicleService = mock(VehicleService.class);
        sackService = mock(SackService.class);
        packService = mock(PackService.class);
        deliveryPointService = new DeliveryPointService(deliveryPointRepository,deliveryPointConverter,vehicleService,sackService,packService);
    }

    @Test
    void createDeliveryPoint_IfSaveIsSuccess_ReturnDeliveryPointDto() {
        DeliveryPointRequest request = new DeliveryPointRequest("Example1","DP230001",PlaceOfDelivery.BRANCH.getValue());
        DeliveryPoint savedDeliveryPoint = new DeliveryPoint(UUID.randomUUID(),"Example1","DP230001",PlaceOfDelivery.BRANCH.getValue(), Collections.EMPTY_SET,Collections.EMPTY_SET,Collections.EMPTY_SET);
        DeliveryPointDto deliveryPointDto = new DeliveryPointDto("Example1","DP230001",PlaceOfDelivery.BRANCH, Collections.EMPTY_SET,Collections.EMPTY_SET,Collections.EMPTY_SET);

        when(deliveryPointRepository.existsByCode("DP230001")).thenReturn(Boolean.FALSE);
        when(deliveryPointRepository.save(Mockito.any(DeliveryPoint.class))).thenReturn(savedDeliveryPoint);
        when(deliveryPointConverter.convert(savedDeliveryPoint)).thenReturn(deliveryPointDto);

        DeliveryPointDto result = deliveryPointService.createDeliveryPoint(request);

        assertEquals(deliveryPointDto, result);

        verify(deliveryPointRepository).existsByCode("DP230001");
        verify(deliveryPointRepository).save(Mockito.any(DeliveryPoint.class));
        verify(deliveryPointConverter).convert(savedDeliveryPoint);
    }

    @Test
    void getDeliveryPointWithThisCode_IsItSuccess_ReturnDeliveryPointDto() {
        DeliveryPoint getDeliveryPoint = generateDeliveryPoints(1, PlaceOfDelivery.BRANCH, VehicleState.LOADING).iterator().next();
        DeliveryPointDto deliveryPointDto = generateDeliveryPointDtos(Collections.singleton(getDeliveryPoint)).iterator().next();

        when(deliveryPointRepository.findByCode("DP230001")).thenReturn(Optional.of(getDeliveryPoint));
        when(deliveryPointConverter.convertWithAllParam(getDeliveryPoint)).thenReturn(deliveryPointDto);

        DeliveryPointDto result = deliveryPointService.getDeliveryPointWithThisCode("DP230001");

        assertEquals(deliveryPointDto, result);

        verify(deliveryPointRepository).findByCode("DP230001");
        verify(deliveryPointConverter).convertWithAllParam(getDeliveryPoint);
    }



    @Test
    void getAllDeliveryPointsInThisState_IsItSuccess_ReturnDeliveryPointDtos() {
        Set<DeliveryPoint> deliveryPoints = generateDeliveryPoints(5, PlaceOfDelivery.DISTRIBUTION_CENTER, VehicleState.AVAILABLE);
        Set<DeliveryPointDto> deliveryPointDtos = generateDeliveryPointDtos(deliveryPoints);

        when(deliveryPointRepository.findByPlaceOfDeliveryOrderByCreatedAtDesc(PlaceOfDelivery.DISTRIBUTION_CENTER.getValue())).thenReturn(deliveryPoints);
        when(deliveryPointConverter.convertWithAllParam(deliveryPoints)).thenReturn(deliveryPointDtos);

        Set<DeliveryPointDto> result = deliveryPointService.getAllDeliveryPointsInThisState(PlaceOfDelivery.DISTRIBUTION_CENTER);

        assertEquals(deliveryPointDtos, result);
        verify(deliveryPointRepository).findByPlaceOfDeliveryOrderByCreatedAtDesc(PlaceOfDelivery.DISTRIBUTION_CENTER.getValue());
        verify(deliveryPointConverter).convertWithAllParam(deliveryPoints);
    }
}