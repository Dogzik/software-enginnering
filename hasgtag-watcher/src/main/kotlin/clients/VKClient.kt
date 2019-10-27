package clients

import com.beust.klaxon.Klaxon
import configs.VKConfig
import kotlinx.coroutines.withTimeout
import models.VKResponse

class VKClient(private val httpClient: AsyncHttpClient, private val config: VKConfig) : AutoCloseable {
    private val parser: Klaxon = Klaxon()

    suspend fun getResponse(hashTag: String, startTime: Long, endTime: Long): VKResponse? {
        val query = "${config.schema}://${config.host}:${config.port}/method/newsfeed.search?" +
                "q=%23$hashTag&" +
                "v=${config.version.major}.${config.version.minor}&" +
                "access_token=${config.accessToken}&" +
                "count=0&" +
                "start_time=$startTime&" +
                "end_time=$endTime"
        return try {
            withTimeout(config.timeout.toMillis()) {
                val rawResponse = httpClient.get(query)
                parser.parse<VKResponse>(rawResponse)
            }
        } catch (e: Throwable) {
            null
        }
    }

    override fun close() {
        httpClient.close()
    }
}