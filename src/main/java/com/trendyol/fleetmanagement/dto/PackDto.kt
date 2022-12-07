package com.trendyol.fleetmanagement.dto

import com.trendyol.fleetmanagement.model.PackState


data class PackDto(
    val createdAt: String?,
    val state: PackState?,
    val barcode: String?,
    val desi: Int?
) {
    data class Builder constructor(
        var createdAt: String? = null,
        var state: PackState? = null,
        var barcode: String?= null,
        var desi: Int? = 0
    ) {
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun state(state: PackState) = apply { this.state = state }
        fun barcode(barcode: String) = apply { this.barcode = barcode }
        fun desi(desi: Int) = apply { this.desi = desi }
        fun build() = PackDto(createdAt,state, barcode, desi)
    }
}
