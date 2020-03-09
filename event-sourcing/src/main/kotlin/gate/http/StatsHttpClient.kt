package gate.http

import org.joda.time.LocalDateTime

interface StatsHttpClient {
    suspend fun sendVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int): String
}
