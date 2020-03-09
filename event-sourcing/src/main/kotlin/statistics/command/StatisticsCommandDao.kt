package statistics.command

import org.joda.time.LocalDateTime

interface StatisticsCommandDao {
    fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int)
}
