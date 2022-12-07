package com.trendyol.fleetmanagement.service;

import com.trendyol.fleetmanagement.TestSupport;
import com.trendyol.fleetmanagement.dto.SackDto;
import com.trendyol.fleetmanagement.dto.converter.SackConverter;
import com.trendyol.fleetmanagement.model.Sack;
import com.trendyol.fleetmanagement.model.SackState;
import com.trendyol.fleetmanagement.repository.SackRepository;
import com.trendyol.fleetmanagement.request.SackRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SackServiceTest extends TestSupport {

    private SackConverter sackConverter;

    private SackRepository sackRepository;

    private SackService sackService;

    private PackService packService;

    @BeforeEach
    public void setUp() {
        sackConverter = mock(SackConverter.class);
        sackRepository = mock(SackRepository.class);
        packService = mock(PackService.class);
        sackService = new SackService(sackRepository, packService, sackConverter);
    }

    @Test
    void createSack_IfSaveIsSuccess_ReturnSackDto(){
        SackRequest request = new SackRequest("C725791",40);
        Sack savedSack = new Sack(UUID.randomUUID(),SackState.CREATED.getValue(), "C725791", 40, 40, 0, Collections.EMPTY_SET, null);
        SackDto sackDto = new SackDto(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), SackState.CREATED, "C725791", 40 , 40, 0, Collections.EMPTY_SET);

        when(sackRepository.save(Mockito.any(Sack.class))).thenReturn(savedSack);
        when(sackConverter.convert(savedSack)).thenReturn(sackDto);

        SackDto result = sackService.createSack(request);

        assertEquals(sackDto, result);

        verify(sackRepository).save(Mockito.any(Sack.class));
        verify(sackConverter).convert(savedSack);
    }

    @Test
    void getAllSacksInThisState_IsItSuccess_ReturnSackDtos() {
        Set<Sack> sacks = generateSacks(5, SackState.CREATED);
        Set<SackDto> sackDtos = generateSackDtos(sacks);

        when(sackRepository.findByStateOrderByCreatedAtDesc(SackState.CREATED.getValue())).thenReturn(sacks);
        when(sackConverter.convertWithAllParam(sacks)).thenReturn(sackDtos);

        Set<SackDto> result = sackService.getAllSacksInThisState(SackState.CREATED);

        assertEquals(sackDtos, result);
        verify(sackRepository).findByStateOrderByCreatedAtDesc(SackState.CREATED.getValue());
        verify(sackConverter).convertWithAllParam(sacks);
    }
}