package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.dto.SackDto;
import com.trendyol.fleetmanagement.dto.converter.SackConverter;
import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.Pack;
import com.trendyol.fleetmanagement.model.Sack;
import com.trendyol.fleetmanagement.model.SackState;
import com.trendyol.fleetmanagement.model.Vehicle;
import com.trendyol.fleetmanagement.repository.SackRepository;
import com.trendyol.fleetmanagement.request.PackToSackRequest;
import com.trendyol.fleetmanagement.request.SackRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Set;

@Service
public class SackService {

    private final Logger log = LogManager.getLogger(this.getClass());

    private final SackRepository sackRepository;

    private final SackConverter sackConverter;

    private final PackService packService;

    public SackService(SackRepository sackRepository, PackService packService, SackConverter sackConverter) {
        this.sackRepository = sackRepository;
        this.sackConverter = sackConverter;
        this.packService = packService;
    }

    public SackDto createSack(SackRequest request) {
        Sack sack = new Sack.Builder()
                .sackBarcode(request.barcode())
                .desi(request.desi())
                .freeDesi(request.desi())
                .build();
        sack = sackRepository.save(sack);

        log.info("Created Sack with Barcode {} and Desi {}.", sack.getSackBarcode(), sack.getDesi());
        return sackConverter.convert(sack);
    }

    public Set<SackDto> getAllSacksInThisState(SackState state) {
        return sackConverter.convertWithAllParam(sackRepository.findByStateOrderByCreatedAtDesc(state.getValue()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public SackDto addPackToSack(PackToSackRequest request) {
        return sackRepository.findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(request.sackBarcode(), request.sackDesi())
                .stream()
                .map(sack -> throwErrorIfNotThisState(sack, SackState.CREATED))
                .peek(sack -> isThereDesiInSack(sack.getFreeDesi(), request.pack().packDesi()))
                .peek(sack -> putItInSack(sack, request.pack().packBarcode(), request.pack().packDesi()))
                .findFirst()
                .map(sackConverter::convertWithAllParam)
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, request.sackBarcode(), request.sackDesi()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public SackDto removePackToSack(PackToSackRequest request) {
        return sackRepository.findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(request.sackBarcode(), request.sackDesi())
                .stream()
                .map(sack -> throwErrorIfNotThisState(sack, SackState.CREATED))
                .peek(sack -> takeItOutInSack(sack, request.pack().packBarcode(), request.pack().packDesi()))
                .findFirst()
                .map(sackConverter::convertWithAllParam)
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, request.sackBarcode(), request.sackDesi()));
    }

    private void putItInSack(Sack sack, String packBarcode, int packDesi) {
        Pack pack = packService.takeToAddPack(packBarcode, packDesi);

        sack.getPacks().add(pack);
        sack.setSize(sack.getSize() + 1);
        sack.setFreeDesi(sack.getFreeDesi() - pack.getDesi());
        sackRepository.save(sack);

        log.info("Pack added to Sack.");
    }

    private void takeItOutInSack(Sack sack, String packBarcode, int packDesi) {
        Pack pack = packService.takeToUnpack(sack.getPacks(), packBarcode, packDesi);

        sack.getPacks().remove(pack);
        sack.setSize(sack.getSize() - 1);
        sack.setFreeDesi(sack.getFreeDesi() + pack.getDesi());
        sackRepository.save(sack);

        log.info("Pack removed to Sack.");
    }

    protected Sack takeSackToPutInVehicle(Set<Sack> sacks, String sackBarcode, int sackDesi, String code) {
        return sackRepository.findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(sackBarcode, sackDesi)
                .stream()
                .peek(this::throwErrorIfSackIsEmpty)
                .map(sack -> throwErrorIfNotThisState(sack, SackState.CREATED))
                .map(sack -> throwErrorIfAnyMatch(sacks, sack))
                .peek(sack -> changeSackStateAndDeliveryPointCode(sack, SackState.CREATED, code))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, sackBarcode, sackDesi));
    }

    protected Sack takeSackToRemoveInVehicle(Set<Sack> sacks, String sackBarcode, int sackDesi) {
        return sackRepository.findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(sackBarcode, sackDesi)
                .stream()
                .map(sack -> throwErrorIfNotThisState(sack, SackState.LOADED))
                .map(sack -> throwErrorIfNoneMatch(sacks,sack))
                .peek(sack -> changeSackStateAndDeliveryPointCode(sack, SackState.CREATED, null))
                .findFirst()
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, sackBarcode, sackDesi));
    }

    protected void changeSackState(Sack sack, SackState state) {
        sack.setState(state.getValue());
        sackRepository.save(sack);

        log.info("Sack state changed to {}.", state);
    }

    protected void changeSackStateAndDeliveryPointCode(Sack sack, SackState state, String deliveryPointCode) {
        sack.setState(state.getValue());
        sack.setDeliveryPointCode(deliveryPointCode);
        sackRepository.save(sack);

        log.info("Sack state changed to {}.", state);
    }

    private void isThereDesiInSack(int freeDesiInSack, int packDesi) {
        if(freeDesiInSack < packDesi)
            throw new FleetManagementException(ErrorMessage.NOT_ENOUGH_SPACE_IN_SACK);
    }

    protected void throwErrorIfSackIsEmpty(Sack sack){
        if(sack.getSize() == 0)
            throw new FleetManagementException(ErrorMessage.EMPTY_SACKS_CANNOT_BE_ADDED, sack.getSackBarcode(), sack.getDesi());
    }

    public Sack throwErrorIfNotThisState(Sack sack, SackState sackState) {
        if (sackState.getValue() != sack.getState())
            throw new FleetManagementException(ErrorMessage.SACK_CANNOT_BE_USED, sack.getSackBarcode(), sack.getDesi(), sackState.name());

        return sack;
    }

    private Sack throwErrorIfAnyMatch(Set<Sack> sacks, Sack sack) {
        if (sacks.stream().anyMatch(s -> Objects.equals(s, sack)))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Vehicle.class.getSimpleName(), Sack.class.getSimpleName());

        return sack;
    }

    private Sack throwErrorIfNoneMatch(Set<Sack> sacks, Sack sack) {
        if (sacks.stream().noneMatch(s -> Objects.equals(s, sack)))
            throw new FleetManagementException(ErrorMessage.THIS_DOES_NOT_CONTAIN_THIS, Vehicle.class.getSimpleName(), Sack.class.getSimpleName());

        return sack;
    }
}
