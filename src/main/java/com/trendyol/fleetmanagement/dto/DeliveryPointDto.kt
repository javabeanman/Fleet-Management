package com.trendyol.fleetmanagement.dto

import com.trendyol.fleetmanagement.model.PlaceOfDelivery

data class DeliveryPointDto(
    val name: String?,
    val code: String?,
    val placeOfDelivery: PlaceOfDelivery?,
    val packs: Set<PackDto>?,
    val sacks: Set<SackDto>?,
    val vehicles: Set<VehicleDto>?
){
    data class Builder constructor(
        var name: String? = null,
        var code: String? = null,
        var placeOfDelivery: PlaceOfDelivery? = null,
        var packs: Set<PackDto>? = emptySet(),
        var sacks: Set<SackDto>? = emptySet(),
        var vehicles: Set<VehicleDto>? = emptySet()
    ) {
        fun name(name: String) = apply { this.name = name }
        fun code(code: String) = apply { this.code = code }
        fun placeOfDelivery(placeOfDelivery: PlaceOfDelivery) = apply { this.placeOfDelivery = placeOfDelivery }
        fun packs(packs: Set<PackDto>) = apply { this.packs = packs }
        fun sacks(sacks: Set<SackDto>) = apply { this.sacks = sacks }
        fun vehicles(vehicles: Set<VehicleDto>) = apply { this.vehicles = vehicles }
        fun build() = DeliveryPointDto(name, code, placeOfDelivery, packs, sacks, vehicles)
    }
}
