package users.http

interface ExchangeHttpClient : AutoCloseable {
    suspend fun getSharesPrices(companies: List<String>): List<Long>
    suspend fun buyShares(company: String, count: Long): Long
    suspend fun sellShares(company: String, count: Long): Long
}
