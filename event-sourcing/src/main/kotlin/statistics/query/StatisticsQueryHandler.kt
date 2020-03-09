package statistics.query

import common.Handler

class StatisticsQueryHandler(private val dao: StatisticsQueryDao) : Handler<StatisticsQuery> {
    override suspend fun doHandle(task: StatisticsQuery): String =
        when (task) {
            is UserStatisticsQuery -> {
                val stats = dao.getUserStatistics(task.userId)
                if (stats == null) {
                    "No such user"
                } else {
                    val normalizedTime = stats.totalTimeSpent.normalizedStandard()
                    val timeStr = "${normalizedTime.years} years, " +
                            "${normalizedTime.months} months, " +
                            "${normalizedTime.weeks} weeks, " +
                            "${normalizedTime.days} days, " +
                            "${normalizedTime.hours} hours, " +
                            "${normalizedTime.minutes} minutes, " +
                            "${normalizedTime.seconds} seconds"
                    val averageVisit = normalizedTime.toStandardMinutes().dividedBy(stats.totalVisits).minutes
                    "Total time spent: $timeStr\n" +
                            "Total visits: ${stats.totalVisits}\n" +
                            "Average visit: $averageVisit minutes\n"
                }
            }
        }
}
