package com.trendyol.fleetmanagement.model

import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
data class DeliveryPoint(

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID?,

    val name: String?,

    @Column(nullable = false, unique = true)
    val code: String?,

    val placeOfDelivery: Int?,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "delivery_point_packs",
        joinColumns = [JoinColumn(name = "delivery_point_id")],
        inverseJoinColumns = [JoinColumn(name = "pack_id")]
    )
    val packs: Set<Pack>? = HashSet(),

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "delivery_point_sacks",
        joinColumns = [JoinColumn(name = "delivery_point_id")],
        inverseJoinColumns = [JoinColumn(name = "sack_id")]
    )
    val sacks: Set<Sack>? = HashSet(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vehicle_delivery_point",
        joinColumns = [JoinColumn(name = "delivery_point_id")],
        inverseJoinColumns = [JoinColumn(name = "vehicle_id")]
    )
    val vehicles: Set<Vehicle>? = HashSet()
) : DateAudit(){

    data class Builder constructor(
        var id: UUID? = null,
        var name: String? = null,
        var code: String? = null,
        var placeOfDelivery: Int? = null,
        var packs: Set<Pack>? = emptySet(),
        var sacks: Set<Sack>? = emptySet(),
        var vehicles: Set<Vehicle>? = emptySet()
    ) {
        fun id(id: UUID) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun code(code: String) = apply { this.code = code }
        fun placeOfDelivery(placeOfDelivery: Int) = apply { this.placeOfDelivery = placeOfDelivery }
        fun packs(packs: Set<Pack>) = apply { this.packs = packs }
        fun sacks(sacks: Set<Sack>) = apply { this.sacks = sacks }
        fun vehicles(vehicles: Set<Vehicle>) = apply { this.vehicles = vehicles }
        fun build() = DeliveryPoint(id, name, code, placeOfDelivery, packs, sacks, vehicles)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as DeliveryPoint

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , code = $code , placeOfDelivery = $placeOfDelivery )"
    }
}

enum class PlaceOfDelivery(val value: Int){
    BRANCH(1), DISTRIBUTION_CENTER(2), TRANSFER_CENTER(3);

    companion object {
        fun fromInt(value: Int) = PlaceOfDelivery.values().first { it.value == value }
    }
}