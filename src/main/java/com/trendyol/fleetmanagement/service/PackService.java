package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.dto.PackDto;
import com.trendyol.fleetmanagement.dto.converter.PackConverter;
import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.Pack;
import com.trendyol.fleetmanagement.model.PackState;
import com.trendyol.fleetmanagement.model.Sack;
import com.trendyol.fleetmanagement.model.Vehicle;
import com.trendyol.fleetmanagement.repository.PackRepository;
import com.trendyol.fleetmanagement.request.PackRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class PackService {

    private final Logger log = LogManager.getLogger(this.getClass());

    private final PackRepository packRepository;

    private final PackConverter packConverter;

    public PackService(PackRepository packRepository, PackConverter packConverter) {
        this.packRepository = packRepository;
        this.packConverter = packConverter;
    }

    public PackDto createPack(PackRequest request) {
        Pack pack = new Pack.Builder()
                .barcode(request.barcode())
                .desi(calculateDesi(request.width(), request.height(), request.length())).build();
        pack = packRepository.save(pack);

        log.info("Created Pack with Barcode {} and Desi {}.", pack.getBarcode(), pack.getDesi());
        return packConverter.convert(pack);
    }

    public Set<PackDto> getAllPacksInThisState(PackState state) {
        return packConverter.convert(packRepository.findByStateOrderByCreatedAtDesc(state.getValue()));
    }

    protected Pack takePackToPutInVehicle(Set<Pack> packs, String barcode, int desi, String code){
        return packRepository.findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc(barcode, desi)
                .stream()
                .map(pack -> throwErrorIfNotThisState(pack, PackState.CREATED))
                .map(pack -> throwErrorIfAnyMatch(packs, pack))
                .peek(pack -> changePackStateAndDeliveryPointCode(pack, PackState.LOADED, code))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, barcode, desi));
    }

    protected Pack takePackToRemoveInVehicle(Set<Pack> packs, String barcode, int desi){
        return packRepository.findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc(barcode, desi)
                .stream()
                .map(pack -> throwErrorIfNotThisState(pack, PackState.LOADED))
                .map(pack -> throwErrorIfNoneMatch(packs, pack))
                .peek(pack -> changePackStateAndDeliveryPointCode(pack, PackState.CREATED, null))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, barcode, desi));
    }

    protected Pack takeToAddPack(String barcode, int desi){
        return packRepository.findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc(barcode, desi)
                .stream()
                .map(pack -> throwErrorIfNotThisState(pack, PackState.CREATED))
                .peek(pack -> changePackState(pack, PackState.LOADED_INTO_SACK))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, barcode, desi));
    }

    protected Pack takeToUnpack(Set<Pack> packs, String barcode, int desi){
        return packRepository.findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc(barcode, desi)
                .stream()
                .map(pack -> throwErrorIfDoesNotContain(packs, pack))
                .peek(pack -> changePackState(pack, PackState.CREATED))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, barcode, desi));
    }

    protected void changePackState(Pack pack, PackState state) {
        pack.setState(state.getValue());
        packRepository.save(pack);

        log.info("Pack state changed to {}", state);
    }

    protected void changePackStateAndDeliveryPointCode(Pack pack, PackState state, String code) {
        pack.setState(state.getValue());
        pack.setDeliveryPointCode(code);
        packRepository.save(pack);

        log.info("Pack state changed to {}", state);
    }

    private Pack throwErrorIfNotThisState(Pack pack, PackState packState) {
        if (packState.getValue() != pack.getState())
            throw new FleetManagementException(ErrorMessage.PACK_CANNOT_BE_USED, pack.getBarcode(), pack.getDesi(), packState.name());

        return pack;
    }

    private Pack throwErrorIfDoesNotContain(Set<Pack> packs, Pack pack) {
        if (packs.stream().noneMatch(p -> Objects.equals(p,pack)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Sack.class.getSimpleName(), Pack.class.getSimpleName());

        return pack;
    }

    private Pack throwErrorIfAnyMatch(Set<Pack> packs, Pack pack) {
        if (packs.stream().anyMatch(p -> Objects.equals(p, pack)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Vehicle.class.getSimpleName(), Pack.class.getSimpleName());

        return pack;
    }

    private Pack throwErrorIfNoneMatch(Set<Pack> packs, Pack pack) {
        if (packs.stream().noneMatch(p -> Objects.equals(p, pack)))
            throw new FleetManagementException(ErrorMessage.THIS_DOES_NOT_CONTAIN_THIS, Vehicle.class.getSimpleName(), Pack.class.getSimpleName());

        return pack;
    }

    private int calculateDesi(int width, int height, int length){
        return (int) Math.ceil((width * height * length)/3000.0);
    }
}
