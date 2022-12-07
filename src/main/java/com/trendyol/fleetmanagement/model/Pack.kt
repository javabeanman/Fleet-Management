package com.trendyol.fleetmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
data class Pack(

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID?,

    var state: Int? = PackState.CREATED.value,

    val barcode:String?,

    val desi: Int?,

    @get:JsonIgnore
    var deliveryPointCode: String?

    ): DateAudit() {

    data class Builder constructor(
        var id: UUID? = null,
        var state: Int? = PackState.CREATED.value,
        var barcode: String? = null,
        var desi: Int? = 0,
        var deliveryPointCode: String? = null
    ) {
        fun id(id: UUID) = apply { this.id = id }
        fun state(state: Int) = apply { this.state = state }
        fun barcode(barcode: String) = apply { this.barcode = barcode }
        fun desi(desi: Int) = apply { this.desi = desi }

        fun build() = Pack(id, state, barcode, desi,deliveryPointCode)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Pack

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , createdAt = $createdAt , updatedAt = $updatedAt , state = $state , barcode = $barcode , desi = $desi )"
    }
}

enum class PackState(val value: Int) {
    CREATED(1), LOADED_INTO_SACK(2), LOADED(3), UNLOADED(4);

    companion object {
        fun fromInt(value: Int) = PackState.values().first { it.value == value }
    }
}