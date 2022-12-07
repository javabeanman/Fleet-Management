package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.TestSupport;
import com.trendyol.fleetmanagement.dto.PackDto;
import com.trendyol.fleetmanagement.dto.converter.PackConverter;
import com.trendyol.fleetmanagement.model.Pack;
import com.trendyol.fleetmanagement.model.PackState;
import com.trendyol.fleetmanagement.repository.PackRepository;
import com.trendyol.fleetmanagement.request.PackRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackServiceTest extends TestSupport {


    private PackConverter packConverter;
    private PackRepository packRepository;
    private PackService packService;

    @BeforeEach
    public void setUp() {
        packConverter = mock(PackConverter.class);
        packRepository = mock(PackRepository.class);
        packService = new PackService(packRepository, packConverter);
    }

    @Test
    void createPack_IfSaveIsSuccess_ReturnPackDto() {
        PackRequest request = new PackRequest("P9988000121",10,10,10);
        Pack savedPack = generatePacks(1, PackState.CREATED).iterator().next();
        PackDto packDto = generatePackDtos(Collections.singleton(savedPack)).iterator().next();

        when(packRepository.save(Mockito.any(Pack.class))).thenReturn(savedPack);
        when(packConverter.convert(savedPack)).thenReturn(packDto);

        PackDto result = packService.createPack(request);

        assertEquals(packDto, result);

        verify(packRepository).save(Mockito.any(Pack.class));
        verify(packConverter).convert(savedPack);
    }

    @Test
    void getAllPacksInThisState_IsItSuccess_ReturnPackDtos() {
        Set<Pack> packs = generatePacks(5, PackState.CREATED);
        Set<PackDto> packDtos = generatePackDtos(packs);

        when(packRepository.findByStateOrderByCreatedAtDesc(PackState.CREATED.getValue())).thenReturn(packs);
        when(packConverter.convert(packs)).thenReturn(packDtos);

        Set<PackDto> result = packService.getAllPacksInThisState(PackState.CREATED);

        assertEquals(packDtos, result);
        verify(packRepository).findByStateOrderByCreatedAtDesc(PackState.CREATED.getValue());
        verify(packConverter).convert(packs);
    }

    @Test
    void getPack_IsItSuccess_ReturnPackDto() {
        Pack getPack = generatePacks(1, PackState.CREATED).iterator().next();

        when(packRepository.findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc("P9988000121",1)).thenReturn(Optional.of(getPack));

        Optional<Pack> result = packService.getPack("P9988000121",1);

        assertEquals(Optional.of(getPack), result);

        verify(packRepository).findFirstByBarcodeAndAndDesiOrderByCreatedAtDesc("P9988000121",1);
    }

    @Test
    void changePackState_IsItSuccess_Ok() {
        Pack pack = generatePacks(1, PackState.CREATED).iterator().next();

        when(packRepository.save(Mockito.any(Pack.class))).thenReturn(pack);

        packService.changePackState(pack, PackState.CREATED);
    }

    @Test
    void changePackStateAndDeliveryPointCode_IsItSuccess_Ok() {
        Pack pack = generatePacks(1, PackState.CREATED).iterator().next();

        when(packRepository.save(Mockito.any(Pack.class))).thenReturn(pack);

        packService.changePackStateAndDeliveryPointCode(pack, PackState.UNLOADED, "DP230001");
    }
}