package com.trendyol.fleetmanagement.dto.converter;
import com.trendyol.fleetmanagement.dto.SackDto;
import com.trendyol.fleetmanagement.model.Sack;
import com.trendyol.fleetmanagement.model.SackState;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SackConverter {

    private final PackConverter packConverter;

    public SackConverter(PackConverter packConverter) {
        this.packConverter = packConverter;
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public SackDto convert(Sack sack) {
        return new SackDto.Builder()
                .createdAt(sack.getCreatedAt().format(dateTimeFormatter))
                .state(SackState.Companion.fromInt(sack.getState()))
                .sackBarcode(sack.getSackBarcode())
                .desi(sack.getDesi())
                .size(sack.getSize())
                .build();
    }

    public SackDto convertWithAllParam(Sack sack) {
        return new SackDto.Builder()
                .createdAt(sack.getCreatedAt().format(dateTimeFormatter))
                .state(SackState.Companion.fromInt(sack.getState()))
                .sackBarcode(sack.getSackBarcode())
                .desi(sack.getDesi())
                .freeDesi(sack.getFreeDesi())
                .size(sack.getSize())
                .packs(sack.getPacks().stream()
                        .map(packConverter::convert)
                        .collect(Collectors.toSet()))
                .build();
    }

    public Set<SackDto> convert(Set<Sack> sacks) {
        if(sacks.isEmpty())
            return Collections.emptySet();

        return sacks.stream()
                .map(this::convert)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<SackDto> convertWithAllParam(Set<Sack> sacks) {
        if(sacks.isEmpty())
            return Collections.emptySet();

        return sacks.stream()
                .map(this::convertWithAllParam)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
