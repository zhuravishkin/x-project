package com.zhuravishkin.core.core_http_client_ntlm.repository

import com.zhuravishkin.core.core_http_client_ntlm.domain.RecordStatus
import com.zhuravishkin.core.core_http_client_ntlm.domain.entity.RecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RecordRepository : JpaRepository<RecordEntity, Long> {
    @Query("select r from RecordEntity r where r.status = :status")
    fun findByStatus(status: RecordStatus): List<RecordEntity>
}
