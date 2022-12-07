package com.trendyol.fleetmanagement.dto

import com.trendyol.fleetmanagement.model.VehicleState

data class VehicleDto(
    val plate: String?,
    val state: VehicleState?,
    val packs: Set<PackDto>?,
    val sacks: Set<SackDto>?,
    val deliveryPoints: Set<DeliveryPointDto>?
){
    data class Builder constructor(
        var plate: String? = null,
        var state: VehicleState? = null,
        var packs: Set<PackDto>? = emptySet(),
        var sacks: Set<SackDto>? = emptySet(),
        var deliveryPoints: Set<DeliveryPointDto>? = emptySet()
    ) {
        fun plate(plate: String) = apply { this.plate = plate }
        fun state(state: VehicleState) = apply { this.state = state }
        fun packs(packs: Set<PackDto>) = apply { this.packs = packs }
        fun sacks(sacks: Set<SackDto>) = apply { this.sacks = sacks }
        fun deliveryPoints(deliveryPoints: Set<DeliveryPointDto>) = apply { this.deliveryPoints = deliveryPoints }
        fun build() = VehicleDto(plate, state, packs, sacks, deliveryPoints)
    }
}
