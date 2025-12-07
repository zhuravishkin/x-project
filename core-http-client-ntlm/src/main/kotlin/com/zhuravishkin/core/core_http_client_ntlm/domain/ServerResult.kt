package com.zhuravishkin.core.core_http_client_ntlm.domain

sealed class ServerResult<out T> {
    data class Success<T>(val response: T) : ServerResult<T>()
    data class Error(val message: String, val isRetryable: Boolean = false) : ServerResult<Nothing>()
}
