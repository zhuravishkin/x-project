package com.zhuravishkin.core.core_http_client_ntlm.service

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.zhuravishkin.core.core_http_client_ntlm.CoreHttpClientNtlmApplicationTests
import com.zhuravishkin.core.core_http_client_ntlm.domain.RecordStatus
import com.zhuravishkin.core.core_http_client_ntlm.domain.entity.RecordEntity
import com.zhuravishkin.core.core_http_client_ntlm.repository.RecordRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProcessRecordsServiceTest : CoreHttpClientNtlmApplicationTests() {
    @Autowired
    private lateinit var processRecordsService: ProcessRecordsService

    @Autowired
    private lateinit var recordRepository: RecordRepository

    @BeforeEach
    fun setUp() {
        recordRepository.deleteAll()
        wireMockServer.resetAll()
    }

    @Test
    fun `should process record successfully and update status to COMPLETE`() {
        // Given
        val testPayload = """{"data": "test-value", "id": 123}"""
        val record = RecordEntity(testPayload)
        record.status = RecordStatus.CREATED
        val savedRecord = recordRepository.save(record)

        wireMockServer.stubFor(
            post(urlEqualTo("/process"))
                .withHeader("Content-Type", containing("application/json"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"success": true}""")
                )
        )

        // When
        val processedCount = processRecordsService.execute()

        // Then
        assertEquals(1, processedCount)

        val updatedRecord = recordRepository.findById(savedRecord.id!!).orElseThrow()
        assertEquals(RecordStatus.COMPLETE, updatedRecord.status)
        assertNotNull(updatedRecord.updatedAt)

        wireMockServer.verify(
            postRequestedFor(urlEqualTo("/process"))
                .withHeader("Content-Type", containing("application/json"))
        )
    }
}
