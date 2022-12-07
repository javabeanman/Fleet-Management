package com.trendyol.fleetmanagement.dto

import com.trendyol.fleetmanagement.model.SackState

data class SackDto (
    val createdAt: String?,
    val state: SackState?,
    val sackBarcode: String?,
    val desi: Int?,
    val freeDesi: Int?,
    val size: Int?,
    val packs: Set<PackDto>?
){
    data class Builder constructor(
        var createdAt: String? = null,
        var state: SackState? = null,
        var sackBarcode: String? = null,
        var desi: Int? = 0,
        var freeDesi: Int? = 0,
        var size: Int? = 0,
        var packs: Set<PackDto>? = emptySet()
    ) {
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun state(state: SackState) = apply { this.state = state }
        fun sackBarcode(sackBarcode: String) = apply { this.sackBarcode = sackBarcode }
        fun desi(desi: Int) = apply { this.desi = desi }
        fun freeDesi(freeDesi: Int) = apply { this.freeDesi = freeDesi }
        fun size(size: Int) = apply { this.size = size }
        fun packs(packs: Set<PackDto>) = apply { this.packs = packs }
        fun build() = SackDto(createdAt,state,sackBarcode,desi,freeDesi,size,packs)
    }
}