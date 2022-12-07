package com.trendyol.fleetmanagement.controller.v1;

import com.trendyol.fleetmanagement.dto.PackDto;
import com.trendyol.fleetmanagement.model.PackState;
import com.trendyol.fleetmanagement.request.PackRequest;
import com.trendyol.fleetmanagement.service.PackService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/pack")
public class PackController {
    private final PackService packService;

    public PackController(PackService packService) {
        this.packService = packService;

    }

    @PostMapping
    @ApiOperation(value = "Create a Package")
    public ResponseEntity<PackDto> createPack(@Valid @RequestBody PackRequest packRequest) {
        return ResponseEntity.ok(packService.createPack(packRequest));
    }

    @GetMapping(value = "/by-state")
    @ApiOperation(value = "Get a Specific Packs Instance by State")
    public ResponseEntity<Set<PackDto>> getAllPacksInThisState(@RequestParam PackState state) {
        return ResponseEntity.ok(packService.getAllPacksInThisState(state));
    }
}
