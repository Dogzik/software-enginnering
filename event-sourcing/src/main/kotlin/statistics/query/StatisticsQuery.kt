package statistics.query

sealed class StatisticsQuery

data class UserStatisticsQuery(val userId: Int) : StatisticsQuery()
