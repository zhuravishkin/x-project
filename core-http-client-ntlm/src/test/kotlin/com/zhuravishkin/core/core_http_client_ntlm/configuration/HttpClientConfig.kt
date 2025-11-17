package com.zhuravishkin.core.core_http_client_ntlm.configuration

import com.zhuravishkin.core.core_http_client_ntlm.configuration.properties.HttpClientProperties
import org.apache.http.HttpHost
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.net.URI

@TestConfiguration
class HttpClientConfig(
    private val httpClientProperties: HttpClientProperties
) {
    @Bean
    fun credentialsProvider(): CredentialsProvider =
        BasicCredentialsProvider()

    @Bean
    fun ntlmHttpClient(credentialsProvider: CredentialsProvider): CloseableHttpClient {
        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(httpClientProperties.connectTimeoutSeconds * 1000)
            .setSocketTimeout(httpClientProperties.readTimeout * 1000)
            .build()

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            // Не устанавливаем credentials provider, чтобы избежать NTLM
            .build()
    }

    @Bean
    fun httpClientContext(credentialsProvider: CredentialsProvider): HttpClientContext =
        HttpClientContext.create()

    @Bean
    fun targetHost(): HttpHost {
        val uri = URI(httpClientProperties.url)
        return HttpHost(uri.host, uri.port, uri.scheme)
    }
}
