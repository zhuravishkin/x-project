package com.zhuravishkin.core.core_http_client_ntlm.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zhuravishkin.core.core_http_client_ntlm.configuration.properties.HttpClientProperties
import com.zhuravishkin.core.core_http_client_ntlm.domain.RecordStatus
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
import org.springframework.transaction.annotation.Transactional

@Service
class ProcessRecordsService(
    private val recordRepository: RecordRepository,
    private val httpClientProperties: HttpClientProperties,
    private val httpClient: CloseableHttpClient,
    private val httpClientContext: HttpClientContext,
    private val targetHost: HttpHost,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(): Int {
        logger.info("Start processing records with status CREATED")

        val records = recordRepository.findByStatus(RecordStatus.CREATED)
        logger.info("Records found for processing: {}", records.size)

        if (records.isEmpty()) {
            logger.debug("No records to process")
            return 0
        }

        var processedCount = 0

        records.forEach { record ->
            try {
                if (processRecord(record)) {
                    record.markAsComplete(record.id)
                    recordRepository.save(record)
                    processedCount++
                    logger.debug("Record with ID {} processed successfully", record.id)
                } else {
                    logger.warn("Failed to process record with ID {}", record.id)
                }
            } catch (e: Exception) {
                logger.error("Error while processing record with ID {}: {}", record.id, e.message, e)
            }
        }

        logger.info("Processing finished. Successfully processed records: {}", processedCount)
        return processedCount
    }

    private fun processRecord(record: RecordEntity): Boolean {
        logger.debug("Processing record ID={}, payload={}", record.id, record.payload)

        val jsonRequest = objectMapper.writeValueAsString(record)

        val httpPost = HttpPost(httpClientProperties.path).apply {
            entity = StringEntity(jsonRequest, ContentType.APPLICATION_JSON)
        }

        httpClient.execute(targetHost, httpPost, httpClientContext).use { response ->
            val statusCode = response.statusLine.statusCode
            logger.info("response: $response")

            when (statusCode) {
                in 200..299 ->
                    return true

                in 400..499 -> {
                    logger.error("Record {}: client error, statusCode={}", record.id, statusCode)
                    throwDomain("Client error while calling external service: $statusCode")
                }

                in 500..599 -> {
                    logger.error("Record {}: server error, statusCode={}", record.id, statusCode)
                    throwDomain("Server error while calling external service: $statusCode")
                }

                else -> {
                    logger.error("Record {}: unknown HTTP status: {}", record.id, statusCode)
                    throwDomain("Unknown HTTP status: $statusCode")
                }
            }
        }
    }
}

private fun throwDomain(error: String): Nothing = throw IllegalStateException(error)
