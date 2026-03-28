package com.zhuravishkin.core.core_transaction_synchronization.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ExtendWith(OutputCaptureExtension::class)
class TransactionSynchronizationDemoTest {

    @Autowired
    lateinit var service: TransactionSynchronizationDemo

    @Test
    fun `afterCommit is called when transaction commits`(output: CapturedOutput) {
        service.processAccount(1L)

        TestTransaction.flagForCommit()
        TestTransaction.end()

        assertThat(output).contains("Transaction committed for account: 1")
    }

    @Test
    fun `afterCommit is not called when transaction rolls back`(output: CapturedOutput) {
        service.processAccount(1L)

        TestTransaction.flagForRollback()
        TestTransaction.end()

        assertThat(output).doesNotContain("Transaction committed for account: 1")
    }
}
