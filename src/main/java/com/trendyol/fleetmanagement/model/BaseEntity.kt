package com.trendyol.fleetmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@MappedSuperclass
abstract class DateAudit(

        @get:JsonIgnore
        open var createdAt: LocalDateTime? = null,

        @get:JsonIgnore
        open var updatedAt: LocalDateTime? = null
) {
        @PrePersist
        fun prePersist(){
                createdAt = LocalDateTime.now()
        }

        @PreUpdate
        fun preUpdate(){
                updatedAt = LocalDateTime.now()
        }
}