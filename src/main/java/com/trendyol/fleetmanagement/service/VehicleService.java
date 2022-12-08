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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class VehicleService {

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
                            .forEach(deliveryPointRequest -> addDeliveryPointToVehicle(vehicle, deliveryPointRequest.deliveryPointCode())))
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
                        .stream()
                        .peek(deliveryPointRequest -> throwErrorIfAnyMatch(vehicle.getSacks(), vehicle.getPacks(), deliveryPointRequest.deliveryPointCode()))
                        .forEach(deliveryPointRequest -> removeDeliveryPointToVehicle(vehicle, deliveryPointRequest.deliveryPointCode())))
                .peek(this::arePacksAndSacksEmpty)
                .map(vehicleConverter::convertWithAllParam)
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.VEHICLE_NOT_FOUND, request.vehiclePlate()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public VehicleDto addPacksAndSacksToVehicle(PackAndSackToVehicleRequest request){
        return vehicleRepository.findByPlate(request.vehiclePlate())
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADING))
                .peek(vehicle -> deliveryPointService.throwErrorIfNoneMatch(vehicle.getDeliveryPoints(), request.deliveryPointCode()))
                .peek(vehicle -> request.sackRequests()
                        .forEach(sackRequest -> addSackToVehicle(vehicle, sackRequest.sackBarcode(), sackRequest.sackDesi(), request.deliveryPointCode())))
                .peek(vehicle -> request.packRequests()
                        .forEach(packRequest -> addPackToVehicle(vehicle, packRequest.packBarcode(), packRequest.packDesi(), request.deliveryPointCode())))
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
                .peek(vehicle -> deliveryPointService.throwErrorIfNoneMatch(vehicle.getDeliveryPoints(), request.deliveryPointCode()))
                .peek(vehicle -> request.sackRequests()
                        .forEach(sackRequest -> removeSackToVehicle(vehicle, sackRequest.sackBarcode(), sackRequest.sackDesi())))
                .peek(vehicle -> request.packRequests()
                        .forEach(packRequest -> removePackToVehicle(vehicle, packRequest.packBarcode(), packRequest.packDesi())))
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
                .peek(this::throwErrorIfPacksAndSacksAreEmpty)
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

    private void addSackToVehicle(Vehicle vehicle, String sackBarcode, int sackDesi, String code) {
        Sack sack = sackService.takeSackToPutInVehicle(vehicle.getSacks(), sackBarcode, sackDesi, code);
        vehicle.getSacks().add(sack);

        log.info("Sack added to Vehicle.");
    }

    private void removeSackToVehicle(Vehicle vehicle, String sackBarcode, int sackDesi) {
        Sack sack = sackService.takeSackToRemoveInVehicle(vehicle.getSacks(), sackBarcode, sackDesi);
        vehicle.getSacks().remove(sack);

        log.info("Sack removed to Vehicle.");
    }

    private void addPackToVehicle(Vehicle vehicle, String packBarcode, int packDesi, String code) {
        Pack pack = packService.takePackToPutInVehicle(vehicle.getPacks(), packBarcode, packDesi, code);
        vehicle.getPacks().add(pack);

        log.info("Pack added to Vehicle.");
    }

    private void removePackToVehicle(Vehicle vehicle, String barcode, int packDesi) {
        Pack pack = packService.takePackToRemoveInVehicle(vehicle.getPacks(), barcode, packDesi);
        vehicle.getPacks().remove(pack);

        log.info("Pack removed to Vehicle.");
    }

    protected Stream<Vehicle> distributeVehicle(String plate) {
        return vehicleRepository.findByPlate(plate)
                .stream()
                .map(vehicle -> throwErrorIfNotThisState(vehicle, VehicleState.LOADED))
                .peek(vehicle -> vehicle.getDeliveryPoints()
                        .stream()
                        .peek(deliveryPoint -> deliveryPointService.distributeSacks(vehicle.getSacks(), deliveryPoint, vehicle))
                        .peek(deliveryPoint -> deliveryPointService.distributePacks(vehicle.getPacks(), deliveryPoint, vehicle))
                        .peek(deliveryPoint -> deliveryPointService.throwErrorIfAnyMatch(deliveryPoint, vehicle))
                        .forEach(deliveryPointService::saveDeliveryPoint))
                .peek(this::arePacksAndSacksEmpty);
    }

    private void throwErrorIfAnyMatch(Set<Sack> sacks, Set<Pack> packs, String code){
        if(Stream.concat(sacks.stream().map(Sack::getDeliveryPointCode), packs.stream().map(Pack::getDeliveryPointCode))
                .anyMatch(deliveryPointCode -> Objects.equals(deliveryPointCode, code)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, DeliveryPoint.class.getSimpleName(), Sack.class.getSimpleName() + " or " + Pack.class.getSimpleName());
    }
    private void arePacksAndSacksEmpty(Vehicle vehicle){
        if(vehicle.getSacks().isEmpty() && vehicle.getPacks().isEmpty())
            changeVehicleState(vehicle, VehicleState.AVAILABLE);
        else
            changeVehicleState(vehicle, VehicleState.FAULTY_LOADS_LEFT_IN_VEHICLE);
    }

    private void throwErrorIfPacksAndSacksAreEmpty(Vehicle vehicle){
        if(vehicle.getSacks().isEmpty() && vehicle.getPacks().isEmpty())
            throw new FleetManagementException(ErrorMessage.NO_PACK_OR_SACK_ADDED);
    }

    protected void removePackToVehicle(Vehicle vehicle, Pack pack){
        vehicle.getPacks().remove(pack);
        log.info("Pack removed to Vehicle.");
    }

    protected void removeSackToVehicle(Vehicle vehicle, Sack sack){
        vehicle.getSacks().remove(sack);
        log.info("Sack removed to Vehicle.");
    }

    protected Vehicle throwErrorIfNotThisState(Vehicle vehicle, VehicleState vehicleState) {
        if (vehicleState.getValue() != vehicle.getState())
            throw new FleetManagementException(ErrorMessage.VEHICLE_CANNOT_BE_USED, vehicle.getPlate(), vehicleState.name());

        return vehicle;
    }

    private void addDeliveryPointToVehicle(Vehicle vehicle, String code){
        vehicle.getDeliveryPoints()
                .add(deliveryPointService.takeDeliveryPointToAdd(vehicle.getDeliveryPoints(), code));

        log.info("Delivery Point added to Vehicle.");
    }

    private void removeDeliveryPointToVehicle(Vehicle vehicle, String code){
        vehicle.getDeliveryPoints()
                .remove(deliveryPointService.takeDeliveryPointToRemove(vehicle.getDeliveryPoints(), code));

        log.info("Delivery Point removed to Vehicle.");
    }
}
