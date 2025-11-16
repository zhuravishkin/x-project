package com.zhuravishkin.core.core_http_client_ntlm

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "50s")
class CoreHttpClientNtlmApplication

fun main(args: Array<String>) {
    runApplication<CoreHttpClientNtlmApplication>(*args)
}
