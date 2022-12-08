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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeliveryPointService {

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
            throw new FleetManagementException(ErrorMessage.DELIVERY_POINT_ALREADY_EXIST, request.code());
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
        return vehicleService.distributeVehicle(request.plate())
                .map(Vehicle::getDeliveryPoints)
                .map(deliveryPoints -> deliveryPoints
                        .stream()
                        .map(deliveryPointConverter::convertWithAllParam)
                        .collect(Collectors.toSet()))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND, request.plate()));
    }

    protected void saveDeliveryPoint(DeliveryPoint deliveryPoint) {
        deliveryPointRepository.save(deliveryPoint);
    }

    protected void distributeSacks(Set<Sack> sacks, DeliveryPoint deliveryPoint, Vehicle vehicle) {
        sacks.stream()
                .filter(sack -> Objects.equals(sack.getDeliveryPointCode(), deliveryPoint.getCode()))
                .filter(sack -> !Objects.equals(deliveryPoint.getPlaceOfDelivery(), PlaceOfDelivery.BRANCH.getValue()))
                .peek(sack -> vehicleService.removeSackToVehicle(vehicle, sack))
                .peek(sack -> sackService.changeSackState(sack, SackState.UNLOADED))
                .forEach(sack -> addSackToDeliveryPoint(deliveryPoint, sack));
    }

    protected void distributePacks(Set<Pack> packs, DeliveryPoint deliveryPoint, Vehicle vehicle) {
        packs.stream()
                .filter(pack -> Objects.equals(pack.getDeliveryPointCode(), deliveryPoint.getCode()))
                .filter(pack -> !Objects.equals(deliveryPoint.getPlaceOfDelivery(), PlaceOfDelivery.TRANSFER_CENTER.getValue()))
                .peek(pack -> vehicleService.removePackToVehicle(vehicle, pack))
                .peek(pack -> packService.changePackState(pack, PackState.UNLOADED))
                .forEach(pack -> addPackToDeliveryPoint(deliveryPoint, pack));
    }

    private void addSackToDeliveryPoint(DeliveryPoint deliveryPoint, Sack sack) {
        deliveryPoint.getSacks().add(sack);
        log.info("Sack added to Delivery Point.");
    }

    private void addPackToDeliveryPoint(DeliveryPoint deliveryPoint, Pack pack) {
        deliveryPoint.getPacks().add(pack);
        log.info("Pack added to Delivery Point.");
    }

    protected DeliveryPoint takeDeliveryPointToAdd(Set<DeliveryPoint> deliveryPoints, String code) {
        return deliveryPointRepository.findByCode(code)
                .stream()
                .map(deliveryPoint -> throwErrorIfAnyMatch(deliveryPoints, deliveryPoint))
                .peek(deliveryPoint -> log.info("Delivery Point added to Vehicle."))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, code));
    }

    protected DeliveryPoint takeDeliveryPointToRemove(Set<DeliveryPoint> deliveryPoints, String code) {
        return deliveryPointRepository.findByCode(code)
                .stream()
                .map(deliveryPoint -> throwErrorIfNoneMatch(deliveryPoints, deliveryPoint))
                .peek(deliveryPoint -> log.info("Delivery Point removed to Vehicle."))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, code));
    }

    private DeliveryPoint throwErrorIfAnyMatch(Set<DeliveryPoint> deliveryPoints, DeliveryPoint deliveryPoint) {
        if (deliveryPoints.stream().anyMatch(dp -> Objects.equals(dp, deliveryPoint)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Vehicle.class.getSimpleName(), DeliveryPoint.class.getSimpleName());

        return deliveryPoint;
    }

    protected void throwErrorIfNoneMatch(Set<DeliveryPoint> deliveryPoints, String code) {
        deliveryPointRepository.findByCode(code)
                .stream()
                .map(deliveryPoint -> throwErrorIfNoneMatch(deliveryPoints, deliveryPoint))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, code));
    }

    private DeliveryPoint throwErrorIfNoneMatch(Set<DeliveryPoint> deliveryPoints, DeliveryPoint deliveryPoint) {
        if (deliveryPoints.stream().noneMatch(dp -> Objects.equals(dp, deliveryPoint)))
            throw new FleetManagementException(ErrorMessage.THIS_DOES_NOT_CONTAIN_THIS, Vehicle.class.getSimpleName(), DeliveryPoint.class.getSimpleName());

        return deliveryPoint;
    }

    protected void throwErrorIfAnyMatch(DeliveryPoint deliveryPoint, Vehicle vehicle){
        if(Stream.concat(vehicle.getSacks().stream().map(Sack::getDeliveryPointCode), vehicle.getPacks().stream().map(Pack::getDeliveryPointCode))
                .noneMatch(code -> Objects.equals(code, deliveryPoint.getCode()))){
            removeVehicleToDeliveryPoint(deliveryPoint, vehicle);
        }
    }

    private void removeVehicleToDeliveryPoint(DeliveryPoint deliveryPoint, Vehicle vehicle) {
        deliveryPoint.getVehicles().remove(vehicle);
        log.info("Vehicle removed to Delivery Point.");
    }
}
