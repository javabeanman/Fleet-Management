package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.dto.DeliveryPointDto;
import com.trendyol.fleetmanagement.dto.converter.DeliveryPointConverter;
import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.*;
import com.trendyol.fleetmanagement.repository.DeliveryPointRepository;
import com.trendyol.fleetmanagement.request.DeliveryPointRequest;
import com.trendyol.fleetmanagement.request.VehicleRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeliveryPointService implements BaseService{

    private final Logger log = LogManager.getLogger(this.getClass());

    private final DeliveryPointRepository deliveryPointRepository;

    private final DeliveryPointConverter deliveryPointConverter;

    private final VehicleService vehicleService;

    private final SackService sackService;

    private final PackService packService;

    public DeliveryPointService(DeliveryPointRepository deliveryPointRepository, DeliveryPointConverter deliveryPointConverter, @Lazy VehicleService vehicleService, SackService sackService, PackService packService) {
        this.deliveryPointRepository = deliveryPointRepository;
        this.deliveryPointConverter = deliveryPointConverter;
        this.vehicleService = vehicleService;
        this.sackService = sackService;
        this.packService = packService;
    }

    public DeliveryPointDto createDeliveryPoint(DeliveryPointRequest request) throws FleetManagementException {
        boolean registeredCode = deliveryPointRepository.existsByCode(request.code());

        if(registeredCode){
            throw new FleetManagementException(ErrorMessage.DELIVERY_POINT_ALREADY_EXIST);
        }

        DeliveryPoint deliveryPoint = new DeliveryPoint.Builder()
                .name(request.name())
                .code(request.code())
                .placeOfDelivery(request.placeOfDelivery())
                .build();
        deliveryPoint = deliveryPointRepository.save(deliveryPoint);

        log.info("Created Delivery Point with Code {}.", deliveryPoint.getCode());
        return deliveryPointConverter.convert(deliveryPoint);
    }

    public DeliveryPointDto getDeliveryPointWithThisCode(String code) throws FleetManagementException {
        return deliveryPointRepository.findByCode(code)
                .map(deliveryPointConverter::convertWithAllParam)
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, code));
    }

    public Set<DeliveryPointDto> getAllDeliveryPointsInThisState(PlaceOfDelivery placeOfDelivery) {
        return deliveryPointConverter.convertWithAllParam(deliveryPointRepository.findByPlaceOfDeliveryOrderByCreatedAtDesc(placeOfDelivery.getValue()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public Set<DeliveryPointDto> distributeToDeliveryPoints(VehicleRequest request) throws FleetManagementException {
        return vehicleService.getVehicle(request.plate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADED))
                .peek(vehicle -> vehicle.getDeliveryPoints()
                        .stream()
                        .peek(deliveryPoint -> vehicle.getSacks()
                                .stream()
                                .filter(sack -> Objects.equals(sack.getDeliveryPointCode(), deliveryPoint.getCode()))
                                .filter(sack -> !Objects.equals(deliveryPoint.getPlaceOfDelivery(), PlaceOfDelivery.BRANCH.getValue()))
                                .forEach(sack -> addSackToDeliveryPoint(vehicle, deliveryPoint, sack)))
                        .peek(deliveryPoint -> vehicle.getPacks()
                                .stream()
                                .filter(pack -> Objects.equals(pack.getDeliveryPointCode(), deliveryPoint.getCode()))
                                .filter(pack -> !Objects.equals(deliveryPoint.getPlaceOfDelivery(), PlaceOfDelivery.TRANSFER_CENTER.getValue()))
                                .forEach(pack -> addPackToDeliveryPoint(vehicle, deliveryPoint, pack)))
                        .peek(deliveryPoint -> {
                            if(Stream.concat(vehicle.getSacks().stream().map(Sack::getDeliveryPointCode), vehicle.getPacks().stream().map(Pack::getDeliveryPointCode))
                                    .noneMatch(code -> Objects.equals(code, deliveryPoint.getCode()))){
                                deliveryPoint.getVehicles().remove(vehicle);
                            }
                        })
                        .forEach(deliveryPointRepository::save))
                .peek(vehicle -> {
                    if(vehicle.getSacks().isEmpty() && vehicle.getPacks().isEmpty())
                        vehicleService.changeVehicleState(vehicle, VehicleState.AVAILABLE);
                    else
                        vehicleService.changeVehicleState(vehicle, VehicleState.FAULTY_LOADS_LEFT_IN_VEHICLE);
                })
                .map(Vehicle::getDeliveryPoints)
                .map(deliveryPoints -> deliveryPoints
                        .stream()
                        .map(deliveryPointConverter::convertWithAllParam)
                        .collect(Collectors.toSet()))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND));
    }

    private void addSackToDeliveryPoint(Vehicle vehicle, DeliveryPoint deliveryPoint, Sack sack) {
        vehicle.getSacks().remove(sack);
        log.info("Sack removed to Vehicle.");

        deliveryPoint.getSacks().add(sack);
        log.info("Sack added to Delivery Point.");

        sackService.changeSackState(sack, SackState.UNLOADED);
    }

    private void addPackToDeliveryPoint(Vehicle vehicle, DeliveryPoint deliveryPoint, Pack pack) {
        vehicle.getPacks().remove(pack);
        log.info("Pack removed to Vehicle.");

        deliveryPoint.getPacks().add(pack);
        log.info("Pack added to Delivery Point.");

        packService.changePackState(pack, PackState.UNLOADED);
    }

    protected Optional<DeliveryPoint> getDeliveryPoint(String code) {
        return deliveryPointRepository.findByCode(code);
    }
}
