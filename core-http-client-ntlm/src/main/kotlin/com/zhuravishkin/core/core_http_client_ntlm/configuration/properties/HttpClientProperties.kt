package com.zhuravishkin.core.core_http_client_ntlm.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "x.client")
data class HttpClientProperties(
    var url: String = "",
    var path: String = "",
    var username: String = "",
    var password: String = "",
    var connectTimeoutSeconds: Int = 30,
    var readTimeout: Int = 20
)
