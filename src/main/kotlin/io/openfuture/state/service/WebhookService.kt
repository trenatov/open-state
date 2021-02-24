package io.openfuture.state.service

import io.openfuture.state.domain.Transaction
import io.openfuture.state.domain.Wallet
import io.openfuture.state.webhook.ScheduledTransaction

interface WebhookService {

    suspend fun addTransaction(wallet: Wallet, transaction: Transaction)

    suspend fun addTransactionsFrmDeadQueue(wallet: Wallet)

    suspend fun scheduleNextWebhook(wallet: Wallet)

    suspend fun scheduleFailedWebhook(wallet: Wallet, transaction: ScheduledTransaction)

    suspend fun lock(walletAddress: String): Boolean

    suspend fun unlock(walletAddress: String)

    suspend fun scheduledWallets(): List<String>

    suspend fun firstTransaction(wallet: Wallet): ScheduledTransaction
}