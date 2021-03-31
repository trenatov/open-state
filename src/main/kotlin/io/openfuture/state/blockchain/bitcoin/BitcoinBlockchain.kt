package io.openfuture.state.blockchain.bitcoin

import io.openfuture.state.blockchain.Blockchain
import io.openfuture.state.blockchain.dto.UnifiedBlock
import io.openfuture.state.blockchain.dto.UnifiedTransaction
import io.openfuture.state.util.toLocalDateTimeInSeconds
import org.springframework.stereotype.Component

@Component
class BitcoinBlockchain(private val bitcoinRpcClient: BitcoinRpcClient) : Blockchain() {

    override suspend fun getLastBlockNumber(): Int {
        val latestBlockHash = bitcoinRpcClient.getLatestBlockHash()
        return bitcoinRpcClient.getBlockHeight(latestBlockHash)
    }

    override suspend fun getBlock(blockNumber: Int): UnifiedBlock {
        val blockHash = bitcoinRpcClient.getBlockHash(blockNumber)
        val block = bitcoinRpcClient.getBlock(blockHash)

        return toUnifiedBlock(block)
    }

    private suspend fun toUnifiedBlock(btcBlock: BitcoinBlock): UnifiedBlock {
        return UnifiedBlock(
                toUnifiedTransactions(btcBlock.transactions),
                btcBlock.time.toLocalDateTimeInSeconds(),
                btcBlock.height,
                btcBlock.hash
        )
    }

    private suspend fun toUnifiedTransactions(btcTransactions: List<BitcoinTransaction>): List<UnifiedTransaction> {
        //skip coinbase transaction (miner award)
        return btcTransactions
                .drop(1)
                .flatMap { obtainTransactions(it) }
    }

    private suspend fun obtainTransactions(btcTransaction: BitcoinTransaction): List<UnifiedTransaction> {
        val inputAddresses = btcTransaction.inputs
                .map { bitcoinRpcClient.getInputAddress(it.txId!!, it.outputNumber!!) }
                .toSet()

        return btcTransaction.outputs
                .filter { it.addresses.isNotEmpty() }
                .filter {
                    //skip `change addresses`
                    !it.addresses.any { address -> inputAddresses.contains(address) }
                }
                .map {
                    UnifiedTransaction(
                            btcTransaction.hash,
                            inputAddresses,
                            it.addresses,
                            it.value
                    )
                }
    }

}
