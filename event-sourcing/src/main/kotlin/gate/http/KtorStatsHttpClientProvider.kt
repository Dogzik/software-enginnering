package gate.http

import gate.config.HttpClientConfig
import io.ktor.client.HttpClient

class KtorStatsHttpClientProvider(private val confing: HttpClientConfig) : StatsHttpClientProvider {
    override fun getClient(): StatsHttpClient = KtorStatsHttpClient(HttpClient(), confing)
}
