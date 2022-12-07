package com.trendyol.fleetmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
data class Sack(

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID?,

    var state: Int? = SackState.CREATED.value,

    val sackBarcode: String?,

    val desi: Int? = 0,

    var freeDesi: Int? = 0,

    var size: Int? = 0,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sack_packs",
        joinColumns = [JoinColumn(name = "sack_id")],
        inverseJoinColumns = [JoinColumn(name = "pack_id")]
    )
    val packs: Set<Pack>? = HashSet(),

    @get:JsonIgnore
    var deliveryPointCode: String?

    ): DateAudit() {

    data class Builder constructor(
        var id: UUID? = null,
        var state: Int? = SackState.CREATED.value,
        var sackBarcode: String? = null,
        var desi: Int? = 0,
        var freeDesi: Int? = 0,
        var size: Int? = 0,
        var packs: Set<Pack>? = emptySet(),
        var deliveryPointCode: String? = null) {
        fun id(id: UUID) = apply { this.id = id }
        fun state(state: Int) = apply { this.state = state }
        fun sackBarcode(sackBarcode: String) = apply { this.sackBarcode = sackBarcode }
        fun freeDesi(freeDesi: Int) = apply { this.freeDesi = freeDesi }
        fun desi(desi: Int) = apply { this.desi = desi }
        fun size(size: Int) = apply { this.size = size }
        fun packs(packs: Set<Pack>) = apply { this.packs = packs }

        fun build() = Sack(id,state,sackBarcode,freeDesi,desi,size,packs,deliveryPointCode)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Sack

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , createdAt = $createdAt , updatedAt = $updatedAt , state = $state , sackBarcode = $sackBarcode , desi = $desi , size = $size )"
    }
}

enum class SackState(val value: Int) {
    CREATED(1), LOADED(3), UNLOADED(4);

    companion object {
        fun fromInt(value: Int) = SackState.values().first { it.value == value }
    }
}
