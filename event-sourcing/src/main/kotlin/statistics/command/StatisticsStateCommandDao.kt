package statistics.command

import org.joda.time.LocalDateTime
import statistics.state.StatisticsState

class StatisticsStateCommandDao(private val state: StatisticsState) : StatisticsCommandDao {
    override fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int) =
        state.addVisit(userId, startTime, endTime, eventId)
}
