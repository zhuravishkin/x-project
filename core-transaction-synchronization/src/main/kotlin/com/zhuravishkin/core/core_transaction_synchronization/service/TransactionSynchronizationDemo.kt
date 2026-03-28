package com.zhuravishkin.core.core_transaction_synchronization.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Service
class TransactionSynchronizationDemo {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun processAccount(accountId: Long) {
        log.info("Processing account in transaction: {}", accountId)

        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                log.info("Transaction committed for account: {}", accountId)
            }
        })
    }
}
