package com.zhuravishkin.core.core_http_client_ntlm

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(
    classes = [CoreHttpClientNtlmApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
//@Import(HttpClientConfig::class)
@Testcontainers
@Transactional
@ActiveProfiles(profiles = ["test"])
class CoreHttpClientNtlmApplicationTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:18").apply {
            withDatabaseName("test")
            withUsername("test")
            withPassword("test")
        }

        val wireMockServer = WireMockServer(
            WireMockConfiguration.options()
                .dynamicPort()
        ).apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)

            registry.add("x.client.url") { "http://localhost:${wireMockServer.port()}" }
        }
    }
}
