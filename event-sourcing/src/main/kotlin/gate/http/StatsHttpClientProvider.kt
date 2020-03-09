package gate.http

interface StatsHttpClientProvider {
    fun getClient(): StatsHttpClient
}
