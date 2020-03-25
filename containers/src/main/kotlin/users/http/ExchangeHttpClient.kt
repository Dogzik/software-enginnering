package users.http

import exchange.model.SharesPurchase

interface ExchangeHttpClient : AutoCloseable {
    suspend fun getSharesPrices(companies: List<String>): List<Long>
    suspend fun buyShares(company: String, count: Long): SharesPurchase
    suspend fun sellShares(company: String, count: Long): Long
}
