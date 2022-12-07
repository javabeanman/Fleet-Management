package com.trendyol.fleetmanagement.controller.v1;

import com.trendyol.fleetmanagement.dto.SackDto;
import com.trendyol.fleetmanagement.model.Sack;
import com.trendyol.fleetmanagement.model.SackState;
import com.trendyol.fleetmanagement.request.PackToSackRequest;
import com.trendyol.fleetmanagement.request.SackRequest;
import com.trendyol.fleetmanagement.service.SackService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/sack")
public class SackController {

    private final SackService sackService;

    public SackController(SackService sackService) {
        this.sackService = sackService;
    }

    @PostMapping
    @ApiOperation(value = "Create a Sack")
    public ResponseEntity<SackDto> createSack(@Valid @RequestBody SackRequest sackRequest) {
        return ResponseEntity.ok(sackService.createSack(sackRequest));
    }

    @GetMapping("/by-state")
    @ApiOperation(value = "Get a Specific Sacks Instance by State")
    public ResponseEntity<Set<SackDto>> getAllSacksInThisState(@RequestParam SackState state) {
        return ResponseEntity.ok(sackService.getAllSacksInThisState(state));
    }

    @PostMapping("/add-pack-to-sack")
    @ApiOperation(value = "Add Pack To Sack")
    public ResponseEntity<SackDto> addPackToSack(@Valid @RequestBody PackToSackRequest packToSackRequest) {
        return ResponseEntity.ok(sackService.addPackToSack(packToSackRequest));
    }

    @PostMapping("/remove-pack-to-sack")
    @ApiOperation(value = "Remove Pack To Sack")
    public ResponseEntity<SackDto> removePackToSack(@Valid @RequestBody PackToSackRequest packToSackRequest) {
        return ResponseEntity.ok(sackService.removePackToSack(packToSackRequest));
    }

}
