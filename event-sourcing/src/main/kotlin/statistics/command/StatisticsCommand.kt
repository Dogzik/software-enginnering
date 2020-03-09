package statistics.command

import org.joda.time.LocalDateTime

sealed class StatisticsCommand

data class AddVisitCommand(
    val userId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val eventId: Int
) : StatisticsCommand()
