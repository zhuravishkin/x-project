package com.zhuravishkin.core.core_http_client_ntlm.scheduler

import com.zhuravishkin.core.core_http_client_ntlm.service.ProcessRecordsService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RecordProcessingScheduler(
    private val processRecordsService: ProcessRecordsService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${x.scheduler.spca.cron:0 */5 * * * *}")
    @SchedulerLock(
        name = "processRecordsTask",
        lockAtMostFor = "4m",
        lockAtLeastFor = "5s"
    )
    fun processRecords() {
        log.info("RecordProcessingScheduler start")
        try {
            val processedCount = processRecordsService.sendRecord()
            log.info("Scheduled task completed. Processed records: {}", processedCount)
        } catch (e: Exception) {
            log.error("Error while executing scheduled task: {}", e.message, e)
        }
        log.info("RecordProcessingScheduler finish")
    }
}
