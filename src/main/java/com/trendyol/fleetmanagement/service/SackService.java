package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.dto.SackDto;
import com.trendyol.fleetmanagement.dto.converter.SackConverter;
import com.trendyol.fleetmanagement.exception.ErrorMessage;
import com.trendyol.fleetmanagement.exception.FleetManagementException;
import com.trendyol.fleetmanagement.model.Pack;
import com.trendyol.fleetmanagement.model.PackState;
import com.trendyol.fleetmanagement.model.Sack;
import com.trendyol.fleetmanagement.model.SackState;
import com.trendyol.fleetmanagement.repository.SackRepository;
import com.trendyol.fleetmanagement.request.PackToSackRequest;
import com.trendyol.fleetmanagement.request.SackRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Service
public class SackService implements BaseService{

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
                .peek(sack -> {
                    if(sack.getFreeDesi() < request.pack().packDesi())
                        throw new FleetManagementException(ErrorMessage.NOT_ENOUGH_SPACE_IN_SACK);
                })
                .peek(sack -> packService.getPack(request.pack().packBarcode(), request.pack().packDesi())
                        .stream()
                        .map(pack -> throwErrorIfNotThisState(pack, PackState.CREATED))
                        .peek(pack -> addPackInSack(sack, pack))
                        .findFirst()
                        .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, request.pack().packBarcode(), request.pack().packDesi())))
                .findFirst()
                .map(sackConverter::convertWithAllParam)
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, request.sackBarcode(), request.sackDesi()));
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public SackDto removePackToSack(PackToSackRequest request) {
        return sackRepository.findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(request.sackBarcode(), request.sackDesi())
                .stream()
                .map(sack -> throwErrorIfNotThisState(sack, SackState.CREATED))
                .peek(sack ->
                    packService.getPack(request.pack().packBarcode(), request.pack().packDesi())
                        .stream()
                        .map(pack -> throwErrorIfDoesNotContain(sack.getPacks(), pack))
                        .peek(pack -> removePackInSack(sack, pack))
                        .findFirst()
                        .orElseThrow(() -> new FleetManagementException(ErrorMessage.PACK_NOT_FOUND, request.pack().packBarcode(), request.pack().packDesi())))
                .findFirst()
                .map(sackConverter::convertWithAllParam)
                .orElseThrow(() -> new FleetManagementException(ErrorMessage.SACK_NOT_FOUND, request.sackBarcode(), request.sackDesi()));
    }

    private void addPackInSack(Sack sack, Pack pack) {

        packService.changePackState(pack, PackState.LOADED_INTO_SACK);

        sack.getPacks().add(pack);
        sack.setSize(sack.getSize() + 1);
        sack.setFreeDesi(sack.getFreeDesi() - pack.getDesi());
        sackRepository.save(sack);
        log.info("Pack added to Sack.");
    }

    private void removePackInSack(Sack sack, Pack pack) {

        packService.changePackState(pack, PackState.CREATED);

        sack.getPacks().remove(pack);
        sack.setSize(sack.getSize() - 1);
        sack.setFreeDesi(sack.getFreeDesi() + pack.getDesi());
        sackRepository.save(sack);
        log.info("Pack removed to Sack.");
    }

    @NotNull
    private Pack throwErrorIfDoesNotContain(Set<Pack> packs, Pack pack) {
        if (!packs.contains(pack))
            throw new FleetManagementException(ErrorMessage.THIS_CONTAINS_THIS, Sack.class, Pack.class);

        return pack;
    }

    protected Optional<Sack> getSack(String sackBarcode, int desi) {
        return sackRepository.findFirstBySackBarcodeAndDesiOrderByCreatedAtDesc(sackBarcode, desi);
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
}
