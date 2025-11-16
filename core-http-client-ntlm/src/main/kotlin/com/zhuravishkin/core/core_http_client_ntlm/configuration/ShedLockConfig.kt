package com.zhuravishkin.core.core_http_client_ntlm.configuration

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class ShedLockConfig(private val dataSource: DataSource) {
    @Bean
    fun lockProvider(): LockProvider =
        JdbcTemplateLockProvider(dataSource, "cep.shedlock")
}
