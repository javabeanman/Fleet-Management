package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.dto.VehicleDto;
import com.trendyol.fleetmanagement.dto.converter.VehicleConverter;
import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.*;
import com.trendyol.fleetmanagement.repository.VehicleRepository;
import com.trendyol.fleetmanagement.request.DeliveryPointToVehicleRequest;
import com.trendyol.fleetmanagement.request.PackAndSackToVehicleRequest;
import com.trendyol.fleetmanagement.request.VehicleRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class VehicleService implements BaseService{

    private final Logger log = LogManager.getLogger(this.getClass());

    private final VehicleRepository vehicleRepository;

    private final VehicleConverter vehicleConverter;

    private final DeliveryPointService deliveryPointService;

    private final SackService sackService;

    private final PackService packService;

    public VehicleService(VehicleRepository vehicleRepository, VehicleConverter vehicleConverter, @Lazy DeliveryPointService deliveryPointService, SackService sackService, PackService packService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleConverter = vehicleConverter;
        this.deliveryPointService = deliveryPointService;
        this.sackService = sackService;
        this.packService = packService;
    }

    public VehicleDto createVehicle(VehicleRequest request) {
        boolean registeredPlate = vehicleRepository.existsByPlate(request.plate());

        if(registeredPlate){
            throw new FleetManagementException(ErrorMessage.VEHICLE_ALREADY_EXIST, request.plate());
        }

        Vehicle vehicle = new Vehicle.Builder()
                .plate(request.plate())
                .build();
        vehicle = vehicleRepository.save(vehicle);

        log.info("Created Vehicle with Plate {}.", vehicle.getPlate());
        return vehicleConverter.convert(vehicle);
    }

    public VehicleDto getVehicleWithThisPlate(String plate) {
        return vehicleRepository.findByPlate(plate)
                .map(vehicleConverter::convertWithAllParam)
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND, plate));
    }

    public Set<VehicleDto> getAllVehiclesInThisState(VehicleState state) {
        return vehicleConverter.convertWithAllParam(vehicleRepository.findByStateOrderByCreatedAtDesc(state.getValue()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public VehicleDto addDeliveryPointToVehicle(DeliveryPointToVehicleRequest request) {
        return vehicleRepository.findByPlate(request.vehiclePlate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.AVAILABLE))
                .peek(vehicle -> request.deliveryPointCodes()
                            .forEach(dpc -> deliveryPointService.getDeliveryPoint(dpc.deliveryPointCode())
                                    .stream()
                                    .map(deliveryPoint -> throwErrorIfAnyMatch(vehicle.getDeliveryPoints(), deliveryPoint))
                                    .peek(deliveryPoint -> vehicle.getDeliveryPoints().add(deliveryPoint))
                                    .peek(deliveryPoint -> log.info("Delivery Point added to Vehicle."))
                                    .findFirst()
                                    .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, dpc.deliveryPointCode()))))
                .peek(vehicle -> changeVehicleState(vehicle, VehicleState.LOADING))
                .map(vehicleConverter::convertWithAllParam)
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND, request.vehiclePlate()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public VehicleDto removeDeliveryPointToVehicle(DeliveryPointToVehicleRequest request) {
        return vehicleRepository.findByPlate(request.vehiclePlate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADING))
                .peek(vehicle -> request.deliveryPointCodes()
                                .forEach(dpc -> deliveryPointService.getDeliveryPoint(dpc.deliveryPointCode())
                                        .stream()
                                        .map(deliveryPoint -> throwErrorIfNoneMatch(vehicle.getDeliveryPoints(), deliveryPoint))
                                        .peek(deliveryPoint -> {
                                            if(Stream.concat(vehicle.getSacks().stream().map(Sack::getDeliveryPointCode), vehicle.getPacks().stream().map(Pack::getDeliveryPointCode))
                                                    .anyMatch(code -> Objects.equals(code, deliveryPoint.getCode()))){
                                                throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, DeliveryPoint.class, Sack.class.getName() + " or " + Pack.class.getName());
                                            }})
                                        .peek(deliveryPoint -> vehicle.getDeliveryPoints().remove(deliveryPoint))
                                        .peek(deliveryPoint -> log.info("Delivery Point removed to Vehicle."))
                                        .findFirst()
                                        .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, dpc.deliveryPointCode()))))
                .peek(vehicle -> {
                    if(vehicle.getSacks().isEmpty() && vehicle.getPacks().isEmpty()){
                        changeVehicleState(vehicle, VehicleState.AVAILABLE);
                    }})
                .map(vehicleConverter::convertWithAllParam)
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND, request.vehiclePlate()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public VehicleDto addPacksAndSacksToVehicle(PackAndSackToVehicleRequest request){
        return vehicleRepository.findByPlate(request.vehiclePlate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADING))
                .peek(vehicle -> deliveryPointService.getDeliveryPoint(request.deliveryPointCode())
                        .stream()
                        .map(deliveryPoint -> throwErrorIfNoneMatch(vehicle.getDeliveryPoints(), deliveryPoint))
                        .findFirst()
                        .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, request.deliveryPointCode())))
                .peek(vehicle -> request.sackRequests()
                        .forEach(sackRequest -> sackService.getSack(sackRequest.sackBarcode(), sackRequest.sackDesi())
                                .stream()
                                .peek(sack -> {
                                    if(sack.getSize() == 0)
                                        throw new FleetManagementException(ErrorMessage.EMPTY_SACKS_CANNOT_BE_ADDED, sack.getSackBarcode(), sack.getDesi());
                                })
                                .map(sack -> throwErrorIfNotThisState(sack, SackState.CREATED))
                                .map(sack -> throwErrorIfAnyMatch(vehicle.getSacks(), sack))
                                .peek(sack -> addSackToVehicle(vehicle, sack, request.deliveryPointCode()))
                                .findFirst()
                                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, sackRequest.sackBarcode(), sackRequest.sackDesi()))))
                .peek(vehicle -> request.packRequests()
                        .forEach(packRequest -> packService.getPack(packRequest.packBarcode(), packRequest.packDesi())
                                .stream()
                                .map(pack -> throwErrorIfNotThisState(pack, PackState.CREATED))
                                .map(pack -> throwErrorIfAnyMatch(vehicle.getPacks(), pack))
                                .peek(pack -> addPackToVehicle(vehicle, pack, request.deliveryPointCode()))
                                .findFirst()
                                .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, packRequest.packBarcode(), packRequest.packDesi()))))
                .map(vehicleRepository::save)
                .map(vehicleConverter::convertWithAllParam)
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND,request.vehiclePlate()));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public VehicleDto removePacksAndSacksToVehicle(PackAndSackToVehicleRequest request) {
        return vehicleRepository.findByPlate(request.vehiclePlate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADING))
                .peek(vehicle -> deliveryPointService.getDeliveryPoint(request.deliveryPointCode())
                        .stream()
                        .map(deliveryPoint -> throwErrorIfNoneMatch(vehicle.getDeliveryPoints(), deliveryPoint))
                        .findFirst()
                        .orElseThrow(() -> new FleetManagementException(ErrorMessage.DELIVERY_POINT_NOT_FOUND, request.deliveryPointCode())))
                .peek(vehicle -> request.sackRequests()
                        .forEach(sackRequest -> sackService.getSack(sackRequest.sackBarcode(),sackRequest.sackDesi())
                                .stream()
                                .map(sack -> throwErrorIfNotThisState(sack, SackState.LOADED))
                                .map(sack -> throwErrorIfNoneMatch(vehicle.getSacks(),sack))
                                .peek(sack -> removeSackToVehicle(vehicle, sack))
                                .findFirst()
                                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, sackRequest.sackBarcode(), sackRequest.sackDesi()))))
                .peek(vehicle -> request.packRequests()
                        .forEach(packRequest -> packService.getPack(packRequest.packBarcode(), packRequest.packDesi())
                                .stream()
                                .map(pack -> throwErrorIfNotThisState(pack, PackState.LOADED))
                                .map(pack -> throwErrorIfNoneMatch(vehicle.getPacks(), pack))
                                .peek(pack -> removePackToVehicle(vehicle, pack))
                                .findFirst()
                                .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, packRequest.packBarcode(), packRequest.packDesi()))))
                .map(vehicleRepository::save)
                .map(vehicleConverter::convertWithAllParam)
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND, request.vehiclePlate()));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public VehicleDto completeVehicleLoading(VehicleRequest request) {
        return vehicleRepository.findByPlate(request.plate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADING))
                .peek(vehicle -> {
                    if(vehicle.getSacks().isEmpty() && vehicle.getPacks().isEmpty())
                        throw new FleetManagementException(ErrorMessage.NO_PACK_OR_SACK_ADDED);
                })
                .peek(vehicle -> changeVehicleState(vehicle, VehicleState.LOADED))
                .map(vehicleConverter::convertWithAllParam)
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND,request.plate()));
    }

    protected void changeVehicleState(Vehicle vehicle, VehicleState vehicleState) {
        vehicle.setState(vehicleState.getValue());
        vehicleRepository.save(vehicle);

        log.info("Vehicle state changed to {}.", vehicleState);
    }

    private void addSackToVehicle(Vehicle vehicle, Sack sack, String deliveryPointCode) {
        vehicle.getSacks().add(sack);
        log.info("Sack added to Vehicle.");

        sackService.changeSackStateAndDeliveryPointCode(sack, SackState.LOADED, deliveryPointCode);
    }

    private void removeSackToVehicle(Vehicle vehicle, Sack sack) {
        vehicle.getSacks().remove(sack);
        log.info("Sack removed to Vehicle.");

        sackService.changeSackStateAndDeliveryPointCode(sack, SackState.CREATED, null);
    }

    private void addPackToVehicle(Vehicle vehicle, Pack pack, String deliveryPointCode) {
        vehicle.getPacks().add(pack);
        log.info("Pack added to Vehicle.");

        packService.changePackStateAndDeliveryPointCode(pack, PackState.LOADED, deliveryPointCode);
    }

    private void removePackToVehicle(Vehicle vehicle, Pack pack) {
        vehicle.getPacks().remove(pack);
        log.info("Pack removed to Vehicle.");

        packService.changePackStateAndDeliveryPointCode(pack, PackState.CREATED, null);
    }

    @NotNull
    private Pack throwErrorIfAnyMatch(Set<Pack> packs, Pack pack) {
        if (packs.stream().anyMatch(p -> Objects.equals(p, pack)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Vehicle.class, Pack.class);

        return pack;
    }

    @NotNull
    private Sack throwErrorIfAnyMatch(Set<Sack> sacks, Sack sack) {
        if (sacks.stream().anyMatch(s -> Objects.equals(s, sack)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Vehicle.class, Sack.class);

        return sack;
    }

    @NotNull
    private DeliveryPoint throwErrorIfAnyMatch(Set<DeliveryPoint> deliveryPoints, DeliveryPoint deliveryPoint) {
        if (deliveryPoints.stream().anyMatch(dp -> Objects.equals(dp, deliveryPoint)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Vehicle.class, DeliveryPoint.class);

        return deliveryPoint;
    }

    @NotNull
    private Pack throwErrorIfNoneMatch(Set<Pack> packs, Pack pack) {
        if (packs.stream().noneMatch(p -> Objects.equals(p, pack)))
            throw new FleetManagementException(ErrorMessage.THIS_DOES_NOT_CONTAIN_THIS, Vehicle.class, Pack.class);

        return pack;
    }

    @NotNull
    private Sack throwErrorIfNoneMatch(Set<Sack> sacks, Sack sack) {
        if (sacks.stream().noneMatch(s -> Objects.equals(s, sack)))
            throw new FleetManagementException(ErrorMessage.THIS_DOES_NOT_CONTAIN_THIS, Vehicle.class, Sack.class);

        return sack;
    }

    @NotNull
    private DeliveryPoint throwErrorIfNoneMatch(Set<DeliveryPoint> deliveryPoints, DeliveryPoint deliveryPoint) {
        if (deliveryPoints.stream().noneMatch(dp -> Objects.equals(dp, deliveryPoint)))
            throw new FleetManagementException(ErrorMessage.THIS_DOES_NOT_CONTAIN_THIS, Vehicle.class, DeliveryPoint.class);

        return deliveryPoint;
    }

    protected Optional<Vehicle> getVehicle(String plate) {
        return vehicleRepository.findByPlate(plate);
    }
}
