package com.zhuravishkin.core.core_http_client_ntlm.configuration

import com.zhuravishkin.core.core_http_client_ntlm.configuration.properties.HttpClientProperties
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.NTCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
class NtlmHttpClientConfig(
    private val httpClientProperties: HttpClientProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun credentialsProvider(): CredentialsProvider =
        BasicCredentialsProvider().apply {
            setCredentials(
                AuthScope.ANY,
                NTCredentials(httpClientProperties.username, httpClientProperties.password, null, null)
            )
        }

    @Bean
    fun ntlmHttpClient(credentialsProvider: CredentialsProvider): CloseableHttpClient {
        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(httpClientProperties.connectTimeoutSeconds)
            .setSocketTimeout(httpClientProperties.readTimeout)
            .build()

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .setDefaultCredentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    fun httpClientContext(credentialsProvider: CredentialsProvider): HttpClientContext =
        HttpClientContext.create().apply { this.credentialsProvider = credentialsProvider }

    @Bean
    fun targetHost(): HttpHost {
        val uri = URI(httpClientProperties.url)
        return HttpHost(uri.host, uri.port, uri.scheme)
    }
}
