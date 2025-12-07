package com.zhuravishkin.core.core_http_client_ntlm.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zhuravishkin.core.core_http_client_ntlm.configuration.properties.HttpClientProperties
import com.zhuravishkin.core.core_http_client_ntlm.domain.RecordStatus
import com.zhuravishkin.core.core_http_client_ntlm.domain.ServerResult
import com.zhuravishkin.core.core_http_client_ntlm.domain.entity.RecordEntity
import com.zhuravishkin.core.core_http_client_ntlm.repository.RecordRepository
import org.apache.http.HttpHost
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProcessRecordsService(
    private val recordRepository: RecordRepository,
    private val httpClientProperties: HttpClientProperties,
    private val httpClient: CloseableHttpClient,
    private val httpClientContext: HttpClientContext,
    private val targetHost: HttpHost,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendRecord(): Int {
        log.info("Start processing records with status CREATED")

        val records = recordRepository.findByStatus(RecordStatus.CREATED)
        log.info("Records found for processing: {}", records.size)

        var processedCount = 0
        if (records.isEmpty()) {
            log.debug("No records to process")
            return processedCount
        }

        records.forEach { record ->
            try {
                when (processRecord(record)) {
                    is ServerResult.Success<*> -> {
                        record.markAsComplete(record.id)
                        recordRepository.save(record)
                        processedCount++
                        log.debug("Record with ID {} processed successfully", record.id)
                    }

                    is ServerResult.Error -> {
                        log.warn("Failed to process record with ID {}", record.id)
                    }
                }
            } catch (e: Exception) {
                log.error("Error while processing record with ID {}: {}", record.id, e.message, e)
            }
        }

        log.info("Processing finished. Successfully processed records: {}", processedCount)
        return processedCount
    }

    private fun processRecord(record: RecordEntity): ServerResult<Boolean> {
        log.debug("Processing record ID={}, payload={}", record.id, record.payload)

        val jsonRequest = objectMapper.writeValueAsString(record)

        val httpPost = HttpPost(httpClientProperties.path).apply {
            entity = StringEntity(jsonRequest, ContentType.APPLICATION_JSON)
        }

        httpClient.execute(targetHost, httpPost, httpClientContext).use { response ->
            val statusCode = response.statusLine.statusCode
            log.info("response: $response")

            return when (statusCode) {
                in 200..299 ->
                    ServerResult.Success(true)

                in 400..499 -> {
                    log.error("Record {}: client error, statusCode={}", record.id, statusCode)
                    ServerResult.Error("Client error while calling external service: $statusCode")
                }

                in 500..599 -> {
                    log.error("Record {}: server error, statusCode={}", record.id, statusCode)
                    ServerResult.Error("Server error while calling external service: $statusCode")
                }

                else -> {
                    log.error("Record {}: unknown HTTP status: {}", record.id, statusCode)
                    ServerResult.Error("Unknown HTTP status: $statusCode")
                }
            }
        }
    }
}
