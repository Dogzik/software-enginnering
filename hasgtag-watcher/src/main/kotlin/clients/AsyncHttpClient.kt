package clients

interface AsyncHttpClient : AutoCloseable {
    suspend fun get(query: String): String
}