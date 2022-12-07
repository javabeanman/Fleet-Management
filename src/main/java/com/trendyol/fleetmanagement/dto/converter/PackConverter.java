package com.trendyol.fleetmanagement.dto.converter;

import com.trendyol.fleetmanagement.dto.PackDto;
import com.trendyol.fleetmanagement.model.Pack;
import com.trendyol.fleetmanagement.model.PackState;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PackConverter {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public PackDto convert(Pack pack) {
        return new PackDto.Builder()
                .createdAt(pack.getCreatedAt().format(dateTimeFormatter))
                .state(PackState.Companion.fromInt(pack.getState()))
                .barcode(pack.getBarcode())
                .desi(pack.getDesi())
                .build();
    }

    public Set<PackDto> convert(Set<Pack> packs) {
        if(packs.isEmpty())
            return Collections.emptySet();

        return packs.stream()
                .map(this::convert)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
