package gate.http

import gate.config.HttpClientConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.joda.time.LocalDateTime


class KtorStatsHttpClient(private val client: HttpClient, private val config: HttpClientConfig) : StatsHttpClient {
    override suspend fun sendVisit(
        userId: Int,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        eventId: Int
    ): String {
        val startStr = timeToString(startTime)
        val endStr = timeToString(endTime)
        val url = "${config.schema}://${config.host}:${config.port}/command/add_visit?" +
                "user_id=$userId&start_time=$startStr&end_time=$endStr&event_id=$eventId"
        return client.get(url)
    }

    companion object {
        fun timeToString(time: LocalDateTime): String = time.toString("yyyy-MM-dd'T'HH:mm:ss")
    }
}
