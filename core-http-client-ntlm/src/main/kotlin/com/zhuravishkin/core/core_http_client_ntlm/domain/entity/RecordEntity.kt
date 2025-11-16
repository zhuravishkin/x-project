package com.zhuravishkin.core.core_http_client_ntlm.domain.entity

import com.zhuravishkin.core.core_http_client_ntlm.domain.RecordStatus
import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(schema = "cep", name = "records")
class RecordEntity(
    @Column(name = "payload", nullable = false, updatable = false, length = 1000)
    val payload: String
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RecordStatus = RecordStatus.CREATED

    @Column(name = "external_id")
    var externalId: Long? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    @PrePersist
    fun onPersist() {
        createdAt = Instant.now()
        updatedAt = createdAt
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }

    fun markAsComplete(externalId: Long?) {
        this.status = RecordStatus.COMPLETE
        this.externalId = externalId
        this.updatedAt = Instant.now()
    }
}
