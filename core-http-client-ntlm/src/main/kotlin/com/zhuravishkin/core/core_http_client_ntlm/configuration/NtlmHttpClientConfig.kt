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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
class NtlmHttpClientConfig(
    private val httpClientProperties: HttpClientProperties
) {
    @Bean
    fun ntlmHttpClient(): CloseableHttpClient {
        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(httpClientProperties.connectTimeoutSeconds * 1000)
            .setSocketTimeout(httpClientProperties.readTimeout * 1000)
            .build()

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .setDefaultCredentialsProvider(credentialsProvider())
            .build()
    }

    @Bean
    fun httpClientContext(): HttpClientContext =
        HttpClientContext.create().apply { this.credentialsProvider = credentialsProvider() }

    @Bean
    fun targetHost(): HttpHost {
        val uri = URI(httpClientProperties.url)
        return HttpHost(uri.host, uri.port, uri.scheme)
    }

    private fun credentialsProvider(): CredentialsProvider =
        BasicCredentialsProvider().apply {
            setCredentials(
                AuthScope.ANY,
                NTCredentials(httpClientProperties.username, httpClientProperties.password, null, null)
            )
        }
}
