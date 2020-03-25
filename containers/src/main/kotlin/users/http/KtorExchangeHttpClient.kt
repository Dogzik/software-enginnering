package users.http

import exchange.model.Shares
import exchange.model.SharesPurchase
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import users.config.ExchangeClientConfig

class KtorExchangeHttpClient(config: ExchangeClientConfig) : ExchangeHttpClient {
    override suspend fun getSharesPrices(companies: List<String>): List<Long> {
        val futures = coroutineScope {
            companies.map {
                async {
                    val url = urlBuilder()
                        .addPath("get_shares")
                        .append("?")
                        .addCompany(it)
                        .toString()
                    getString(url)
                }
            }
        }
        return futures.map { parser.parse(Shares.serializer(), it.await()).price }
    }

    override suspend fun buyShares(company: String, count: Long): SharesPurchase {
        val url = urlBuilder()
            .addPath("buy_shares")
            .append("?")
            .addCompany(company)
            .append("&")
            .addCount(count)
            .toString()
        return parser.parse(SharesPurchase.serializer(), getString(url))
    }

    override suspend fun sellShares(company: String, count: Long): Long {
        val url = urlBuilder()
            .addPath("sell_shares")
            .append("?")
            .addCompany(company)
            .append("&")
            .addCount(count)
            .toString()
        return getString(url).toLong()
    }

    override fun close() {
        client.close()
    }

    private val client = HttpClient()
    private val base = "http://${config.host}:${config.port}"

    private fun urlBuilder(): StringBuilder = StringBuilder(base)

    private suspend fun getString(url: String): String {
        val response = client.get<HttpResponse>(url)
        val content = response.readText()
        check(response.status == HttpStatusCode.OK) { "Server error: $content" }
        return content
    }

    private companion object {
        fun StringBuilder.addPath(path: String): StringBuilder = append("/$path")
        fun StringBuilder.addCompany(company: String): StringBuilder = append("company=$company")
        fun StringBuilder.addCount(count: Long): StringBuilder = append("count=$count")

        val parser = Json(JsonConfiguration.Stable)
    }
}
