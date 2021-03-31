package io.openfuture.state.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document
data class Wallet(
        @Indexed
        val address: WalletAddress,
        val webhook: String,
        @LastModifiedDate
        var lastUpdate: LocalDateTime = LocalDateTime.now(),
        @MongoId
        val id: String = ObjectId().toHexString()
)
