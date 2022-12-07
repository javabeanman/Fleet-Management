package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.dto.PackDto;
import com.trendyol.fleetmanagement.dto.converter.PackConverter;
import com.trendyol.fleetmanagement.model.Pack;
import com.trendyol.fleetmanagement.model.PackState;
import com.trendyol.fleetmanagement.repository.PackRepository;
import com.trendyol.fleetmanagement.request.PackRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    private int calculateDesi(int width, int height, int length){
        return (int) Math.ceil((width * height * length)/3000.0);
    }

    protected Optional<Pack> getPack(String barcode, Integer desi){
        return packRepository.findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc(barcode, desi);
    }

    protected void changePackState(Pack pack, PackState state) {
        pack.setState(state.getValue());
        packRepository.save(pack);

        log.info("Pack state changed to {}", state);
    }

    protected void changePackStateAndDeliveryPointCode(Pack pack, PackState state, String deliveryPointCode) {
        pack.setState(state.getValue());
        pack.setDeliveryPointCode(deliveryPointCode);
        packRepository.save(pack);

        log.info("Pack state changed to {}", state);
    }
}
