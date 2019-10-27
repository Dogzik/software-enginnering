package clients

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class KtorAsyncHttpClient : AsyncHttpClient {
    private val httpClient = HttpClient()

    override suspend fun get(query: String) = httpClient.get<String>(query)

    override fun close() {
        httpClient.close()
    }
}