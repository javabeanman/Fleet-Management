package com.trendyol.fleetmanagement.model

import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
data class Vehicle(

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID?,

    var state: Int? = VehicleState.AVAILABLE.value,

    @Column(nullable = false, unique = true)
    val plate: String?,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vehicle_packs",
        joinColumns = [JoinColumn(name = "vehicle_id")],
        inverseJoinColumns = [JoinColumn(name = "pack_id")]
    )
    val packs: Set<Pack>? = HashSet(),

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vehicle_sacks",
        joinColumns = [JoinColumn(name = "vehicle_id")],
        inverseJoinColumns = [JoinColumn(name = "sack_id")]
    )
    val sacks: Set<Sack>? = HashSet(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vehicle_delivery_point",
        joinColumns = [JoinColumn(name = "vehicle_id")],
        inverseJoinColumns = [JoinColumn(name = "delivery_point_id")]
    )
    val deliveryPoints: Set<DeliveryPoint>? = HashSet()


) : DateAudit(){

    data class Builder constructor(
        var id: UUID? = null,
        var state: Int? = VehicleState.AVAILABLE.value,
        var plate: String? = null,
        var packs: Set<Pack>? = emptySet(),
        var sacks: Set<Sack>? = emptySet(),
        var deliveryPoints: Set<DeliveryPoint>? = emptySet()
    ) {
        fun id(id: UUID) = apply { this.id = id }
        fun state(state: Int) = apply { this.state = state }
        fun plate(plate: String) = apply { this.plate = plate }
        fun packs(packs: Set<Pack>) = apply { this.packs = packs }
        fun sacks(sacks: Set<Sack>) = apply { this.sacks = sacks }
        fun deliveryPoints(deliveryPoints: Set<DeliveryPoint>) = apply { this.deliveryPoints = deliveryPoints }
        fun build() = Vehicle(id,state,plate,packs,sacks,deliveryPoints)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Vehicle

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , state = $state , plate = $plate )"
    }

}

enum class VehicleState(val value: Int) {
    AVAILABLE(1), LOADING(2), LOADED(3), FAULTY_LOADS_LEFT_IN_VEHICLE(4);

    companion object {
        fun fromInt(value: Int) = VehicleState.values().first { it.value == value }
    }
}